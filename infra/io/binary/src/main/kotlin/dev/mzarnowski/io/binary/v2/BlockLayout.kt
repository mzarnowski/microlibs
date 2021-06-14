package dev.mzarnowski.io.binary.v2

abstract class BlockLayout(override val layout: Layout, override val index: Int) : Member, Layout() {
    override fun writeInt(offset: Int, value: Int, size: Int) {
        layout.writeInt(offset + offset(), value, size)
    }

    override fun writeBytes(offset: Int, bytes: ByteArray, start: Int, len: Int) {
        layout.writeBytes(offset + offset(), bytes, start, len)
    }
}

open class DynamicBlock(layout: Layout, index: Int) : BlockLayout(layout, index) {
    final override fun size(): Int = offsetOf(fieldCount())
    override fun flush() = Unit
    override fun clear() = Unit
}

open class StaticBlock(layout: Layout, index: Int, private val bytes: Int) : BlockLayout(layout, index) {
    final override fun size(): Int = bytes
    override fun flush() = Unit
    override fun clear() = Unit
}

//
open class StreamBlock(layout: Layout, index: Int, limitBytes: Int) : DynamicBlock(layout, index) {
    private val size = int(limitBytes)

    override fun flush() {
        val size = layout.offsetOf(index + 1)
        this.size.write(size)
    }

//    private var length = 0
//
//    override fun flush() = sizeField.write(length)
//    override fun size(): Int = length
//
//    fun append(bytes: ByteArray, start: Int = 0, len: Int = bytes.size - start) {
//        layout.writeBytes(sizeField.size + length, bytes, start, len)
//        length += len
//    }
//
//    override fun clear() {
//        super.clear()
//        length = 0
//    }
}

open class StreamField(override val layout: Layout, override val index: Int, private val sizeField: IntField) : Member {
    private var length = 0

    override fun flush() = sizeField.write(length)
    override fun size(): Int = length

    fun append(bytes: ByteArray, start: Int = 0, len: Int = bytes.size - start) {
        layout.writeBytes(sizeField.size + length, bytes, start, len)
        length += len
    }

    override fun clear() {
        super.clear()
        length = 0
    }
}