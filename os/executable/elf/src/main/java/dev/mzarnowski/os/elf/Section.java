package dev.mzarnowski.os.elf;

public final class Section {
    public interface Type {
    }

    public enum StandardType implements Type {
        UNUSED, PROGRAM, SYM_TAB, STR_TAB, REL_A, HASH_TAB, DYNAMIC_LINKING, NOTE, EMPTY, REL, SHLIB,
        LINKER_SYM_TAB,
        /**
         * Array of pointers to initialization functions
         */
        INIT_ARRAY,
        /**
         * Array of pointers to finalization functions
         */
        FINI_ARRAY,
    }

    public static final class CustomType implements Type {
        final int value;

        CustomType(int value) {
            this.value = value;
        }
    }
}
