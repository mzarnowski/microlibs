package dev.mzarnowski.os.elf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class ElfDecoder {
    static Executable from(ByteBuffer buffer) {
        var partial = rawHeader(buffer);
        var reader = ElfReader.from(partial.arch, buffer);
        return executable(partial, reader);
    }

    private static RawElfHeader rawHeader(ByteBuffer buffer) {
        var magic = buffer.getInt();
        if (magic != 0x7F454C46) throw new IllegalStateException("Invalid magic number: " + Integer.toHexString(magic));

        var arch = arch(buffer);
        var byteOrder = parseByteOrder(buffer);
        if (buffer.get() != 1) throw new IllegalStateException("Not an original version of binary");
        var abi = parseABI(buffer);
        var version = buffer.getLong(); // TODO abiversion values from 0-255, currently unused?

        buffer.order(byteOrder);
        return new RawElfHeader(arch, abi, version);
    }

    private static int arch(ByteBuffer buffer) {
        var next = buffer.get();
        if (next == 1) return 32;
        if (next == 2) return 64;
        throw new IllegalStateException("Unsupported value " + next);
    }

    private static ByteOrder parseByteOrder(ByteBuffer buffer) {
        var next = buffer.get();
        if (next == 1) return ByteOrder.LITTLE_ENDIAN;
        if (next == 2) return ByteOrder.BIG_ENDIAN;
        throw new IllegalStateException("Unsupported value " + next);
    }

    private static ABI parseABI(ByteBuffer buffer) {
        var next = buffer.get();
        if (next == 0) return ABI.SYSTEM_V;
        throw new IllegalStateException("Unsupported value " + next);
    }

    private static Executable executable(RawElfHeader raw, ElfReader reader) {
        var type = executableType(reader);
        var isa = isa(reader);
        if (reader.get4() != 1) throw new IllegalStateException("Not an original version of binary");

        var entryPoint = reader.get();
        var programHeadersAt = reader.get();
        var sectionHeadersAt = reader.get();
        var flags = reader.get4();

        reader.get2(); // ignore header size
        var programHeaderSize = reader.get2();
        var programHeaderCount = reader.get2();
        var sectionHeaderSize = reader.get2();
        var sectionHeaderCount = reader.get2();
        var nameSectionHeader = reader.get2();

        var sectionReader = reader.fork((int) sectionHeadersAt, sectionHeaderCount * sectionHeaderSize);
        var rawSections = SectionHeaderParser.parse(sectionReader, sectionHeaderCount);

        var nameSectionAt = rawSections[nameSectionHeader].offset;
        var names = new SectionNames(reader.buffer.position((int) nameSectionAt));
        var sections = SectionHeaderParser.parse(rawSections, names);

        var programReader = reader.fork((int) programHeadersAt, programHeaderCount * programHeaderSize);
        var programs = ProgramHeaderParser.parse(programReader, programHeaderCount);

        var header = new ElfHeader(raw.arch, raw.abi, raw.abiVersion, type, isa, entryPoint, flags);
        return new Executable(reader, header, sections, programs);
    }

    private static Executable.Type executableType(ElfReader reader) {
        var next = reader.get2();
        if (next == 0x00_00) return Executable.Type.NONE;
        if (next == 0x00_01) return Executable.Type.REL;
        if (next == 0x00_02) return Executable.Type.EXEC;
        if (next == 0x00_03) return Executable.Type.DYN;
        throw new IllegalStateException("Unsupported value " + next);
    }

    private static ISA isa(ElfReader reader) {
        var next = reader.get2();
        if (next == 0x3E) return ISA.amd64;
        throw new IllegalStateException("Unsupported value " + next);
    }
}
