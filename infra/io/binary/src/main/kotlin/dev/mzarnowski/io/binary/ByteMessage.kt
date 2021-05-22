package dev.mzarnowski.io.binary

class ByteMessage {
    private var buffer: Buffer? = null

    private val fields = mutableListOf<Field>()
    private var offsets = IntArray(8)
    private var lastKnownOffset = 0

    fun byte(): IntField = add(::IntField, 1)
    fun short(): IntField = add(::IntField, 2)
    fun int(): IntField = add(::IntField, 4)
    fun long(): LongField = add(::LongField, 8)
    fun block(bytes: Int): BlockField = add(::BlockField, bytes)

    fun stream(limit: Int): StreamField {
        val maxSizeInBytes = when {
            limit < 0xFF -> 1
            limit < 0xFFFF -> 2
            else -> 4
        }

        val sizeField = add(::IntField, maxSizeInBytes)
        val index = fields.size
        return StreamField(this, sizeField, index).also {
            fields += it // note that the offset of the next field is not set since the size of the stream is not known
        }
    }

    fun readFrom(bytes: ByteArray, offset: Int) {
        buffer = Buffer(bytes, offset)
    }

    internal fun readBlock(index: Int): ByteArray {
        calculateOffset(index)
        val offset = offsets[index]
        val limit = offsets[index + 1]
        return buffer?.read(offset, limit) ?: throw IllegalStateException("Input not set")
    }

    internal fun readBlock(index: Int, size: Int): ByteArray {
        calculateOffset(index) // although reading a stream doesn't need it, let's make it like other cases
        val offset = offsets[index]
        val limit = offset + size
        return buffer?.read(offset, limit) ?: throw IllegalStateException("Input not set")
    }

    internal fun readInt(index: Int): Int {
        calculateOffset(index)
        val offset = offsets[index]
        val bytes = offsets[index + 1] - offset
        return buffer?.readInt(offset, bytes) ?: throw IllegalStateException("Input not set")
    }

    internal fun readLong(index: Int): Long {
        calculateOffset(index)
        val offset = offsets[index]
        val bytes = offsets[index + 1] - offset
        return buffer?.readLong(offset, bytes) ?: throw IllegalStateException("Input not set")
    }

    private fun <A : Field> add(field: (ByteMessage, Int, Int) -> A, size: Int): A {
        val index = fields.size
        return field(this, index, size).also {
            fields += it
            if (lastKnownOffset == index) {
                offsets[index + 1] = offsets[index] + size
                lastKnownOffset = index + 1
            }
        }
    }

    private fun calculateOffset(index: Int) {
        // end of a segment is at offset[index + 1], hence <= and not <
        if (lastKnownOffset <= index) {
            var offset = offsets[lastKnownOffset]
            for (fieldIndex in lastKnownOffset..index) {
                offset += fields[fieldIndex].size()
                offsets[fieldIndex + 1] = offset
            }
            lastKnownOffset = index + 1
        }
    }
}
