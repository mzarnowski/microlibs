package dev.mzarnowski.os.elf;

import java.nio.ByteBuffer;

final class SectionNames {
    private final int sectionOffset;
    private final ByteBuffer buffer;

    SectionNames(ByteBuffer buffer) {
        this.buffer = buffer;
        this.sectionOffset = buffer.position();
    }

    String nameAt(int offset) {
        var start = sectionOffset + offset;
        buffer.position(start);
        while (buffer.get() != 0) continue;
        var size = buffer.position() - start;

        var bytes = new byte[size - 1]; // also drop the 0 at the end
        buffer.position(start).get(bytes);
        return new String(bytes);
    }
}
