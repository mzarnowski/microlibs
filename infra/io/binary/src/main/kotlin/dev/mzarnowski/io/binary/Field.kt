package dev.mzarnowski.io.binary

interface Field {
    fun size(): Int
}

// TODO rename ArrayOfBytes
class BlockField(
    private val parent: ByteMessage,
    private val index: Int,
    private val size: Int,
    private val padding: Padding
) : Field {
    fun write(bytes: ByteArray, offset: Int = 0) = parent.writeBlock(index, size, bytes, offset, padding)
    fun readBlock(): ByteArray = parent.readBlock(index)
    override fun size(): Int = size
}

// TODO rename StreamOfBytes
class StreamField(private val parent: ByteMessage, private val size: IntField, private val index: Int) : Field {
    private var position = 0

    fun readBlock(): ByteArray = parent.readBlock(index, size())
    override fun size(): Int = size.read()

    fun append(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size) {
//        parent.writeBlock(position, bytes, offset, length)
//        position += length
    }

    fun flush(){
//        val size = buffer.size

    }
}

class IntField(private val parent: ByteMessage, private val index: Int, private val size: Int) : Field {
    fun write(value: Int) = parent.writeInt(index, value, size)
    fun read(): Int = parent.readInt(index)
    override fun size(): Int = size
}

class LongField(private val parent: ByteMessage, private val index: Int, private val size: Int) : Field {
    fun write(value: Long) = parent.writeLong(index, value, size)
    fun read(): Long = parent.readLong(index)
    override fun size(): Int = size
}