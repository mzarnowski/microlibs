package dev.mzarnowski.io.binary.v5

import java.nio.ByteBuffer

// TODO this is specific instance of a fragment (since it holds a nested buffer)
class Fragment(size: Int, private val bytes: Field.Raw) : Structure() {
    private val buffer = Buffer(ByteBuffer.allocate(size)) // TODO Buffer.OfExactSize
    fun flush() = bytes.write(buffer.bytes) // TODO could use a separate encoding for buffers?
}

open class BufferedFragment(protected val bytes: Field.Raw, size: Int) : Structure() {
    protected val buffer = Buffer(ByteBuffer.allocate(size)) // TODO Buffer.OfExactSize
    open fun flush() = bytes.write(buffer.bytes) // TODO could use a separate encoding for buffers?
}