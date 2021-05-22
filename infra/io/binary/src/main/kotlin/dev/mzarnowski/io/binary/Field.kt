package dev.mzarnowski.io.binary

interface Field {
    fun size(): Int
}

class BlockField(private val parent: ByteMessage, private val index: Int, private val size: Int) : Field {
    fun readBlock(): ByteArray = parent.readBlock(index)
    override fun size(): Int = size
}

class StreamField(private val parent: ByteMessage, private val size: IntField, private val index: Int) : Field {
    fun readBlock(): ByteArray = parent.readBlock(index, size())
    override fun size(): Int = size.read()
}

class IntField(private val parent: ByteMessage, private val index: Int, private val size: Int) : Field {
    fun read(): Int = parent.readInt(index)
    override fun size(): Int = size
}

class LongField(private val parent: ByteMessage, private val index: Int, private val size: Int) : Field {
    fun read(): Long = parent.readLong(index)
    override fun size(): Int = size
}