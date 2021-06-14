package dev.mzarnowski.io.binary.v5

import kotlin.math.max

object RawEncoding : Encoding.Raw {
    override fun write(buffer: Buffer, offset: Int, value: ByteArray, from: Int, len: Int): Int {
        val size = max(len, value.size - from)
        buffer.write(offset, value, from, size)
        return size
    }
}