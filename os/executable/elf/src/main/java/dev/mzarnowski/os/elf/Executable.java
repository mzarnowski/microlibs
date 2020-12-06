package dev.mzarnowski.os.elf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class Executable {
    public final ElfHeader header;
    private final BinaryFile file;
    private final Map<String, SectionHeader> sections;
    private final ProgramHeader[] programs;

    public Executable(BinaryFile file, ElfHeader header, Map<String, SectionHeader> sections, ProgramHeader[] programs) {
        this.file = file;
        this.header = header;
        this.sections = sections;
        this.programs = programs;
    }

    public static Executable from(Path path) {
        var file = new BinaryFile(path);
        return ElfDecoder.from(file);
    }

    public Set<String> sections() {
        return Collections.unmodifiableSet(sections.keySet());
    }

    public List<Program.Type> programs() {
        return Arrays.stream(programs).map(p -> p.type).collect(Collectors.toList());
    }

    public ByteBuffer section(String name) {
        var section = sections.get(name);
        if (section == null) throw new NoSuchElementException("Invalid section: " + name);
        return file.load(section.offset, section.size);
    }

    public ByteBuffer program(int number) {
        if (number < 0 || number >= programs.length) throw new NoSuchElementException("Invalid program: " + number);
        var program = programs[number];
        return file.load(program.offset, program.filesz);
    }

    public enum Type {
        NONE, REL, EXEC, DYN
    }
}
