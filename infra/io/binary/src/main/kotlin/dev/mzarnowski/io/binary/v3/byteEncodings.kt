package dev.mzarnowski.io.binary.v3

import kotlin.math.min

class BlockEncoding(val size: Int, val padding: Padding) : Encoding.OfBytes {
    override fun write(output: ByteArray, offset: Int, value: ByteArray, from: Int, len: Int): Int {
        val available = min(len, size)
        System.arraycopy(value, from, output, offset, len)
        padding.pad(output, offset + available, size - available)
        return available
    }
}

// TODO min > 0 requires padding
class StreamEncoding(val min: Int, val max: Int, val padding: Padding) : Encoding.OfBytes {
    private val sizeEncoding: Encoding.OfInt = TODO("calculate from limit")
    override fun write(output: ByteArray, offset: Int, value: ByteArray, from: Int, len: Int): Int {
        val available = min(len, max)
        val shift = sizeEncoding.write(output, offset, available)
        System.arraycopy(value, from, output, offset + shift, len)
        if (available < min) {
            padding.pad(output, offset + available, min - available)
        }
        return available
    }
}