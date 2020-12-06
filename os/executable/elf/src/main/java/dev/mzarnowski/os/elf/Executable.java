package dev.mzarnowski.os.elf;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class Executable {
    public final ElfHeader header;
    private final ElfReader file;
    private final Map<String, SectionHeader> sections;
    private final ProgramHeader[] programs;

    public Executable(ElfReader file, ElfHeader header, Map<String, SectionHeader> sections, ProgramHeader[] programs) {
        this.file = file;
        this.header = header;
        this.sections = sections;
        this.programs = programs;
    }

    public static Executable from(Path path) throws IOException {
        var file = new RandomAccessFile(path.toFile(), "r");
        try (var inChannel = file.getChannel()) {
            var buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            return ElfDecoder.from(buffer);
        }
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
        return file.fork((int) section.offset, (int) section.size).buffer;
    }

    public ByteBuffer program(int number) {
        if (number < 0 || number >= programs.length) throw new NoSuchElementException("Invalid program: " + number);
        var program = programs[number];
        return file.fork((int) program.offset, (int) program.filesz).buffer;
    }

    public enum Type {
        NONE, REL, EXEC, DYN
    }
}
