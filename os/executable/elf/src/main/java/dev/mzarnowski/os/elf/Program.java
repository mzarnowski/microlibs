package dev.mzarnowski.os.elf;

public final class Program {
    public interface Type {
    }

    public enum StandardType implements Program.Type {
        UNUSED,
        LOAD,
        DYNAMIC,
        INTERPRETER,
        NOTE,
        SHLIB,
        PROGRAM_HEADER_TABLE,
        THREAD_LOCAL_STORAGE
    }

    public static final class CustomType implements Program.Type {
        final int value;

        CustomType(int value) {
            this.value = value;
        }
    }
}
