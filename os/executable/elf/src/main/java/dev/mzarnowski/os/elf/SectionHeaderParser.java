package dev.mzarnowski.os.elf;

import java.util.HashMap;

final class SectionHeaderParser {
    static HashMap<String, SectionHeader> sectionHeaders(ElfReader reader, SectionNames names) {
        var headers = new HashMap<String, SectionHeader>();
        while (reader.hasMore()) {
            var header = new SectionHeader(
                    names.of(reader.get4()),
                    type(reader.get4()),
                    reader.get(),
                    reader.get(),
                    reader.get(),
                    reader.get(),
                    names.of(reader.get4()),
                    reader.get4(),
                    reader.get(),
                    reader.get()
            );
            headers.put(header.name, header);
        }
        return headers;
    }

    private static Section.Type type(int value) {
        if (value == 0x00_00) return Section.StandardType.UNUSED;
        if (value == 0x00_01) return Section.StandardType.PROGRAM;
        if (value == 0x00_02) return Section.StandardType.SYM_TAB;
        if (value == 0x00_03) return Section.StandardType.STR_TAB;
        if (value == 0x00_04) return Section.StandardType.REL_A;
        if (value == 0x00_05) return Section.StandardType.HASH_TAB;
        if (value == 0x00_06) return Section.StandardType.DYNAMIC_LINKING;
        if (value == 0x00_07) return Section.StandardType.NOTE;
        if (value == 0x00_08) return Section.StandardType.EMPTY;
        if (value == 0x00_09) return Section.StandardType.REL;
        if (value == 0x00_0A) return Section.StandardType.SHLIB;
        if (value == 0x00_0B) return Section.StandardType.LINKER_SYM_TAB;
        if (value == 0x00_0E) return Section.StandardType.INIT_ARRAY;
        if (value == 0x00_0F) return Section.StandardType.FINI_ARRAY;
        if (value >= 0xFF_FF) return new Section.CustomType(value);
        throw new IllegalStateException("Unsupported value " + value);
    }
}
