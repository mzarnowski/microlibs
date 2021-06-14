package dev.mzarnowski.io.binary.v3

class Buffer(val bytes: ByteArray, var position: Int) {
    fun write(offset: Int, value: Int, encoding: Encoding.OfInt) {
        encoding.write(bytes, offset, value)
    }

    fun write(at: Int, value: ByteArray, from: Int, len: Int, encoding: Encoding.OfBytes) {
        encoding.write(bytes, at, value, from, len)
    }

    fun <A> write(at: Int, value: A, encoding: Encoding.Of<A>) {
        encoding.write(bytes, at, value)
    }
}