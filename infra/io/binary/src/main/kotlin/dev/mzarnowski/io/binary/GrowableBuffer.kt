package dev.mzarnowski.io.binary

import kotlin.math.max

class GrowableBuffer {
    private var buffer = ByteArray(16)
    internal var size = 0

    fun write(position: Int, bytes: ByteArray, from: Int, length: Int) {
        if (position + length >= bytes.size) {
            buffer = bytes.copyOf(position + length)
        }

        System.arraycopy(bytes, from, buffer, position, length)
        size = max(size, position + length)
    }
}