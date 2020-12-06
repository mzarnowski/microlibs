package dev.mzarnowski.os.elf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class SectionHeaderParser {
    static RawSectionHeader[] parse(ElfReader reader, int count) {
        var raw = new RawSectionHeader[count];
        for (int i = 0; i < count; i++) {
            var section = new RawSectionHeader();
            raw[i] = section;
            section.nameIdx = reader.get4();
            section.type = parseType(reader);
            section.flags = reader.get();
            section.addr = reader.get();
            section.offset = reader.get();
            section.size = reader.get();
            section.link = reader.get4();
            section.info = reader.get4();
            section.alignment = reader.get();
            section.entrySize = reader.get();
        }
        return raw;
    }

    static Map<String, SectionHeader> parse(RawSectionHeader[] rawHeaders, SectionNames nameReader) {
        var names = Arrays.stream(rawHeaders).map(h -> nameReader.nameAt(h.nameIdx)).toArray(String[]::new);
        var headers = new HashMap<String, SectionHeader>();
        for (int i = 0; i < rawHeaders.length; i++) {
            var raw = rawHeaders[i];
            var name = names[i];
            var header = new SectionHeader(name, raw.type, raw.flags, raw.addr, raw.offset, raw.size,
                    names[raw.link], raw.info, raw.alignment, raw.entrySize);

            headers.put(name, header);
        }

        return headers;
    }

    private static Section.Type parseType(ElfReader reader) {
        var next = reader.get4();
        if (next == 0x00_00) return Section.StandardType.UNUSED;
        if (next == 0x00_01) return Section.StandardType.PROGRAM;
        if (next == 0x00_02) return Section.StandardType.SYM_TAB;
        if (next == 0x00_03) return Section.StandardType.STR_TAB;
        if (next == 0x00_04) return Section.StandardType.REL_A;
        if (next == 0x00_05) return Section.StandardType.HASH_TAB;
        if (next == 0x00_06) return Section.StandardType.DYNAMIC_LINKING;
        if (next == 0x00_07) return Section.StandardType.NOTE;
        if (next == 0x00_08) return Section.StandardType.EMPTY;
        if (next == 0x00_09) return Section.StandardType.REL;
        if (next == 0x00_0A) return Section.StandardType.SHLIB;
        if (next == 0x00_0B) return Section.StandardType.LINKER_SYM_TAB;
        if (next == 0x00_0E) return Section.StandardType.INIT_ARRAY;
        if (next == 0x00_0F) return Section.StandardType.FINI_ARRAY;
        if (next >= 0xFF_FF) return new Section.CustomType(next);
        throw new IllegalStateException("Unsupported value " + next);
    }
}
