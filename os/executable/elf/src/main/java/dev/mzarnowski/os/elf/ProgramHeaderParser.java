package dev.mzarnowski.os.elf;

final class ProgramHeaderParser {
    static ProgramHeader[] programHeaders(ElfReader reader, int count) {
        var programs = new ProgramHeader[count];
        for (int i = 0; i < count; i++) {
            int flags = 0;
            var type = type(reader.get4());
            if (reader.is64()) flags = reader.get4();
            var offset = reader.get();
            var vaddr = reader.get();
            var paddr = reader.get();
            var filesz = reader.get();
            var memsize = reader.get();
            if (!reader.is64()) flags = reader.get4();
            var align = reader.get();

            programs[i] = new ProgramHeader(type, flags, offset, vaddr, paddr, filesz, memsize, align);
        }

        return programs;
    }

    static Program.Type type(int value) {
        if (value == 0x00_00) return Program.StandardType.UNUSED;
        if (value == 0x00_01) return Program.StandardType.LOAD;
        if (value == 0x00_02) return Program.StandardType.DYNAMIC;
        if (value == 0x00_03) return Program.StandardType.INTERPRETER;
        if (value == 0x00_04) return Program.StandardType.NOTE;
        if (value == 0x00_05) return Program.StandardType.SHLIB;
        if (value == 0x00_06) return Program.StandardType.PROGRAM_HEADER_TABLE;
        if (value == 0x00_07) return Program.StandardType.THREAD_LOCAL_STORAGE;
        if (value >= 0xFF_FF) return new Program.CustomType(value);
        throw new IllegalStateException("Unsupported value " + value);
    }
}
