package dev.mzarnowski.os.elf;

import java.nio.ByteBuffer;

class ElfReader {
    protected final ByteBuffer buffer;

    ElfReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    static ElfReader from(int elfClass, ByteBuffer buffer) {
        if (elfClass == 32) return new ElfReader(buffer);
        else return new LongElfReader(buffer);
    }

    boolean is64() {
        return false;
    }

    final int get1() {
        return buffer.get();
    }

    final int get2() {
        return buffer.getShort();
    }

    final int get4() {
        return buffer.getInt();
    }

    long get() {
        return buffer.getInt();
    }

    ElfReader fork(int from, int size) {
        return new ElfReader(buffer(from, from + size));
    }

    final protected ByteBuffer buffer(int from, int to) {
        return this.buffer.duplicate()
                .order(this.buffer.order())
                .position(from)
                .limit(to);
    }
}

final class LongElfReader extends ElfReader {
    LongElfReader(ByteBuffer buffer) {
        super(buffer);
    }


    boolean is64() {
        return true;
    }

    @Override
    long get() {
        return buffer.getLong();
    }

    @Override
    ElfReader fork(int from, int size) {
        return new LongElfReader(buffer(from, from + size));
    }
}