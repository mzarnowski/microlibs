package dev.mzarnowski.os.elf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static dev.mzarnowski.os.elf.ProgramHeaderParser.programHeaders;
import static dev.mzarnowski.os.elf.SectionHeaderParser.sectionHeaders;

final class ElfDecoder {
    static Executable from(BinaryFile file) {
        var headerBuffer = loadHeader(file);
        var arch = arch(headerBuffer.get(4));
        var byteOrder = byteOrder(headerBuffer.get(5));
        var abi = abi(headerBuffer.get(7));
        var abiVersion = headerBuffer.get(8);

        headerBuffer.order(byteOrder);
        var reader = ElfReader.from(arch, headerBuffer.position(0x10));
        var type = type(reader.get2());
        var isa = isa(reader.get2());
        if (reader.get4() != 1) throw new IllegalStateException("Invalid version:");
        var entryPoint = reader.get();
        var programHeadersAt = reader.get();
        var sectionHeadersAt = reader.get();
        var flags = reader.get4();

        var header = new ElfHeader(arch, abi, abiVersion, type, isa, entryPoint, flags);

        // read program/section header segments
        reader.get2(); // ignore header size
        var programHeaderSize = reader.get2();
        var programHeaderCount = reader.get2();
        var sectionHeaderSize = reader.get2();
        var sectionHeaderCount = reader.get2();
        var nameSectionHeader = reader.get2();

        // read section headers
        var sectionBuffer = file.load(sectionHeadersAt, (long) sectionHeaderCount * sectionHeaderSize, byteOrder);

        var namesOffset = nameSectionHeader * sectionHeaderSize + (arch == 32 ? 0x10 : 0x18);
        var namesSize = nameSectionHeader * sectionHeaderSize + (arch == 32 ? 0x14 : 0x20);
        var names = arch == 32
                ? new SectionNames(file.load(sectionBuffer.getInt(namesOffset), sectionBuffer.getInt(namesSize)))
                : new SectionNames(file.load(sectionBuffer.getLong(namesOffset), sectionBuffer.getLong(namesSize)));

        var sectionReader = ElfReader.from(arch, sectionBuffer);
        var sectionHeaders = sectionHeaders(sectionReader, names);

        // read program headers
        var programBuffer = file.load(programHeadersAt, (long) programHeaderCount * programHeaderSize, byteOrder);
        var programReader = ElfReader.from(arch, programBuffer);
        var programHeaders = programHeaders(programReader, programHeaderCount);

        return new Executable(file, header, sectionHeaders, programHeaders);
    }

    private static ByteBuffer loadHeader(BinaryFile file) {
        var preamble = file.load(0, 0x40);
        var magic = preamble.getInt(0);
        if (magic != 0x7F454C46) throw new IllegalStateException("Invalid magic number: " + Integer.toHexString(magic));
        var version = preamble.get(6);
        if (version != 1) throw new IllegalStateException("Invalid version: " + version);
        return preamble;
    }

    private static int arch(byte value) {
        if (value == 1) return 32;
        if (value == 2) return 64;
        throw new IllegalStateException("Unsupported value " + value);
    }

    private static ByteOrder byteOrder(byte value) {
        if (value == 1) return ByteOrder.LITTLE_ENDIAN;
        if (value == 2) return ByteOrder.BIG_ENDIAN;
        throw new IllegalStateException("Unsupported value " + value);
    }

    private static ABI abi(byte value) {
        if (value == 0) return ABI.SYSTEM_V;
        throw new IllegalStateException("Unsupported value " + value);
    }

    private static Executable.Type type(int value) {
        if (value == 0x00_00) return Executable.Type.NONE;
        if (value == 0x00_01) return Executable.Type.REL;
        if (value == 0x00_02) return Executable.Type.EXEC;
        if (value == 0x00_03) return Executable.Type.DYN;
        throw new IllegalStateException("Unsupported value " + value);
    }

    private static ISA isa(int value) {
        if (value == 0x3E) return ISA.amd64;
        throw new IllegalStateException("Unsupported value " + value);
    }
}
