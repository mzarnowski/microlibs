package dev.mzarnowski.io.binary

class Buffer(private val bytes: ByteArray, private val offset: Int) {
    fun read(start: Int, end: Int): ByteArray = bytes.copyOfRange(start + offset, end + offset)

    fun readInt(start: Int, size: Int): Int {
        var value = bytes[offset + start].toInt() and 0xFF
        repeat(size - 1) {
            value = value shl 8
            value += bytes[offset + start + it + 1].toInt() and 0xFF
        }
        return value
    }

    fun readLong(start: Int, size: Int): Long {
        var value = bytes[offset + start].toLong() and 0xFF
        repeat(size - 1) {
            value = value shl 8
            value += bytes[offset + start + it + 1].toInt() and 0xFF
        }
        return value
    }
}