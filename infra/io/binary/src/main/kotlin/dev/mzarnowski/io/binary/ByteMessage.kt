package dev.mzarnowski.io.binary

import kotlin.math.min

class ByteMessage {
    private var buffer: Buffer? = null

    private val fields = mutableListOf<Field>()
    private var offsets = IntArray(8)
    private var lastKnownOffset = 0

    // this API should be in a builder, favoring here: (Int, (ByteMessage, Int) -> Field)
    fun byte(): IntField = int(1)
    fun short(): IntField = int(2)
    fun int(): IntField = int(4)

    fun long(): LongField {
        val index = fields.size
        val field = LongField(this, index, 8)
        return registered(field, index)
    }

    // TODO is it a correct default?
    fun block(bytes: Int, padding: Padding = Padding.None): BlockField {
        val index = fields.size
        val field = BlockField(this, index, bytes, padding)
        return registered(field, index)
    }

    fun stream(limit: Int): StreamField {
        val maxSizeInBytes = when {
            limit < 0xFF -> 1
            limit < 0xFFFF -> 2
            else -> 4
        }

        val sizeField = int(maxSizeInBytes)
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

    private fun <A : Field> registered(field: A, index: Int): A {
        fields += field
        if (lastKnownOffset == index) {
            offsets[index + 1] = offsets[index] + field.size()
            lastKnownOffset = index + 1
        }
        return field
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

    fun writeBlock(index: Int, size: Int, bytes: ByteArray, inputOffset: Int, padding: Padding) {
        if (lastKnownOffset < index) TODO("Fail because the correct offset is not known")
        val offset = offsets[index]
        val available = min(size, bytes.size - inputOffset)

        val buffer = buffer ?: throw IllegalStateException("Output not set")
        buffer.write(offset, bytes, inputOffset, available)
        if (available < size) buffer.write(offset + available, padding, size - available) // FIXME Ugly
    }

    fun writeInt(index: Int, value: Int, bytes: Int) {
        if (lastKnownOffset < index) TODO("Fail because the correct offset is not known")
        val offset = offsets[index]
        buffer?.writeInt(offset, value, bytes) ?: throw IllegalStateException("Output not set")
    }

    fun writeLong(index: Int, value: Long, bytes: Int) {
        if (lastKnownOffset < index) TODO("Fail because the correct offset is not known")
        val offset = offsets[index]
        buffer?.writeLong(offset, value, bytes) ?: throw IllegalStateException("Output not set")
    }

    private fun int(size: Int): IntField {
        val index = fields.size
        val field = IntField(this, index, size)
        return registered(field, index)
    }

}
