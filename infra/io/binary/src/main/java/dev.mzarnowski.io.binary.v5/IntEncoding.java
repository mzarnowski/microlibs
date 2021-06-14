package dev.mzarnowski.io.binary.v5;

import org.jetbrains.annotations.NotNull;

public final class IntEncoding implements Encoding.OfInt {
    private final int size;

    public IntEncoding(int size) {
        this.size = size;
    }


    @Override
    public int write(@NotNull Buffer buffer, int offset, int value) {
        switch (size) {
            case 4:
                buffer.write(offset + 3, value >> 24);
            case 3:
                buffer.write(offset + 2, value >> 16);
            case 2:
                buffer.write(offset + 1, value >> 8);
            case 1:
                buffer.write(offset, value);
        }

        return size;
    }
}
