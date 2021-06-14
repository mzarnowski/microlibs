//package dev.mzarnowski.io.binary.v3
//
//import java.util.*
//
//sealed class Layout {
//    private val fields = mutableListOf<Field>()
//    private var offsets = intArrayOf(0)
//    private var lastKnownOffset = 0
//
//    fun int(size: Int): Field.OfInt {
//        val encoding = Encoding.OfInt(size)
//        return fieldOf(encoding)
//    }
//
//    fun bytes(size: Int, padding: Padding = Padding.None): Field.OfBytes = bytes(size, size, padding)
//    fun bytes(min: Int, max: Int, padding: Padding = Padding.None): Field.OfBytes {
//        val encoding = if (min == max) BlockEncoding(max, padding) else StreamEncoding(min, max, padding)
//        return fieldOf(encoding)
//    }
//
//    fun <A> seqOf(encoding: Encoding.Of<A>, size: Encoding.OfInt, sizeInBytes: Boolean): Field.OfSeq<A> {
//        return Field.OfSeq(this, fields.size, SequenceEncoding.Of(encoding, size, sizeInBytes)).also(fields::add)
//    }
//
//    fun fieldOf(encoding: Encoding.OfInt): Field.OfInt {
//        return Field.OfInt(this, fields.size, encoding).also(fields::add)
//    }
//
//    fun fieldOf(encoding: Encoding.OfBytes): Field.OfBytes {
//        return Field.OfBytes(this, fields.size, encoding).also(fields::add)
//    }
//
//    fun <A> fieldOf(encoding: Encoding.Of<A>): Field.Of<A> {
//        return Field.Of(this, fields.size, encoding).also(fields::add)
//    }
//
//    fun root(): Root = when (this) {
//        is Root -> this
//        is Child -> parent.root()
//    }
//
//    internal tailrec fun write(offset: Int = 0, f: (ByteArray, Int) -> Unit): Unit = when (this) {
//        is Child -> write(offset + parent.offsetOf(index), f)
//        is Root -> f(buffer, position + offset)
//    }
//
////    // TODO make internal ?
////    abstract fun write(index: Int, offset: Int, value: Int, encoding: Encoding.OfInt)
////    abstract fun write(index: Int, offset: Int, values: IntArray, encoding: SequenceEncoding.OfInt)
////    abstract fun write(index: Int, offset: Int, values: PrimitiveIterator.OfInt, encoding: SequenceEncoding.OfInt)
////    // TODO int sequences
////
////    abstract fun write(index: Int, offset: Int, value: ByteArray, from: Int, length: Int, encoding: Encoding.OfBytes)
////    abstract fun <A> write(index: Int, offset: Int, value: A, encoding: Encoding.Of<A>)
////    abstract fun <A> write(index: Int, offset: Int, values: Array<A>, encoding: SequenceEncoding.Of<A>)
////    abstract fun <A> write(index: Int, offset: Int, values: Collection<A>, encoding: SequenceEncoding.Of<A>)
////    abstract fun <A> write(index: Int, offset: Int, values: Iterator<A>, encoding: SequenceEncoding.Of<A>)
//
//    fun size(): Int = offsetOf(fields.size + 1)
//
//    fun offsetOf(index: Int): Int {
//        return 0
//    }
//}
//
//class Root(val buffer: ByteArray, val position: Int) : Layout() {
//    override fun write(index: Int, offset: Int, value: Int, encoding: Encoding.OfInt) {
//        encoding.write(buffer, positionOf(index, offset), value)
//    }
//
//    override fun write(index: Int, offset: Int, values: IntArray, encoding: SequenceEncoding.OfInt) {
//        encoding.write(buffer, positionOf(index, offset), values)
//    }
//
//    override fun write(index: Int, offset: Int, values: PrimitiveIterator.OfInt, encoding: SequenceEncoding.OfInt) {
//        encoding.write(buffer, positionOf(index, offset), values)
//    }
//
//    override fun write(index: Int, offset: Int, value: ByteArray, from: Int, length: Int, encoding: Encoding.OfBytes) {
//        encoding.write(buffer, positionOf(index, offset), value, from, length)
//    }
//
//    override fun <A> write(index: Int, offset: Int, value: A, encoding: Encoding.Of<A>) {
//        encoding.write(buffer, positionOf(index, offset), value)
//    }
//
//    override fun <A> write(index: Int, offset: Int, values: Array<A>, encoding: SequenceEncoding.Of<A>) {
//        encoding.write(buffer, positionOf(index, offset), values)
//    }
//
//    override fun <A> write(index: Int, offset: Int, values: Collection<A>, encoding: SequenceEncoding.Of<A>) {
//        encoding.write(buffer, positionOf(index, offset), values)
//    }
//
//    override fun <A> write(index: Int, offset: Int, values: Iterator<A>, encoding: SequenceEncoding.Of<A>) {
//        encoding.write(buffer, positionOf(index, offset), values)
//    }
//
//    private fun positionOf(index: Int, offset: Int) = position + offsetOf(index) + offset
//}
//
//open class Child(override val parent: Layout, val index: Int) : Layout(), Field {
//    override fun write(index: Int, offset: Int, value: Int, encoding: Encoding.OfInt) {
//        parent.write(this.index, positionOf(index, offset), value, encoding)
//    }
//
//    override fun write(index: Int, offset: Int, values: IntArray, encoding: SequenceEncoding.OfInt) {
//        parent.write(this.index, positionOf(index, offset), values, encoding)
//    }
//
//    override fun write(index: Int, offset: Int, values: PrimitiveIterator.OfInt, encoding: SequenceEncoding.OfInt) {
//        parent.write(this.index, positionOf(index, offset), values, encoding)
//    }
//
//    override fun write(index: Int, offset: Int, value: ByteArray, from: Int, length: Int, encoding: Encoding.OfBytes) {
//        parent.write(this.index, positionOf(index, offset), value, from, length, encoding)
//    }
//
//    override fun <A> write(index: Int, offset: Int, value: A, encoding: Encoding.Of<A>) {
//        parent.write(this.index, positionOf(index, offset), value, encoding)
//    }
//
//    override fun <A> write(index: Int, offset: Int, values: Array<A>, encoding: SequenceEncoding.Of<A>) {
//        parent.write(this.index, positionOf(index, offset), values, encoding)
//    }
//
//    override fun <A> write(index: Int, offset: Int, values: Collection<A>, encoding: SequenceEncoding.Of<A>) {
//        parent.write(this.index, positionOf(index, offset), values, encoding)
//    }
//
//    override fun <A> write(index: Int, offset: Int, values: Iterator<A>, encoding: SequenceEncoding.Of<A>) {
//        parent.write(this.index, positionOf(index, offset), values, encoding)
//    }
//
//    protected fun positionOf(index: Int, offset: Int) = offsetOf(index) + offset
//}