package dev.mzarnowski.os.elf;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

final class BinaryFile {
    private final Path path;

    BinaryFile(Path path) {
        this.path = path;
    }

    ByteBuffer load(long offset, long size, ByteOrder order) {
        return load(offset, size).order(order);
    }

    ByteBuffer load(long offset, long size) {
        try (var file = new RandomAccessFile(path.toFile(), "r"); var channel = file.getChannel()) {
            return channel.map(FileChannel.MapMode.READ_ONLY, offset, size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
