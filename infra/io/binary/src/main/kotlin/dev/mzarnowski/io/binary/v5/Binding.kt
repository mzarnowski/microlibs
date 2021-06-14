package dev.mzarnowski.io.binary.v5

import java.nio.ByteBuffer


class Buffer(internal var bytes: ByteBuffer) {
    fun byteArray(): ByteArray = TODO()

    fun write(offset: Int, value: Int) {

    }

    fun write(offset: Int, value: ByteArray, from: Int, len: Int) {
        TODO()
    }

    fun write(offset: Int, value: ByteBuffer) {
        // TODO use offset
        bytes.put(value)
    }
}

typealias Offset = Int

open class Structure {
    private val embedded = mutableListOf<Layout>()
    private val fields = mutableListOf<Field>()
    private var buffer = Buffer(TODO()) // TODO not needed in fragments
    private var sizes = IntArray(2)
    private var offsets = IntArray(2)
    private var lastKnownOffset = 0 // TODO needs "firstStreamIndex" for reinitialization

    fun bind(field: Field): Int {
        val index = fields.size
        fields += field

        if (sizes.size < fields.size) {
            sizes = sizes.copyOf(sizes.size * 2)
            offsets = offsets.copyOf(offsets.size * 2)
        }

        return index
    }

    fun track(layout: Layout): Unit = run { embedded.add(layout) }

    fun write(index: Int, encoding: Encoding.OfInt, value: Int) {
        write(index) { offset -> encoding.write(buffer, offset, value) }
    }

    fun <A> write(index: Int, encoding: Encoding.Of<A>, value: A) {
        write(index) { offset -> encoding.write(buffer, offset, value) }
    }

    fun write(index: Int, encoding: Encoding.Raw, value: ByteArray, from: Int, len: Int) {
        write(index) { offset -> encoding.write(buffer, offset, value, from, len) }
    }

    private inline fun write(index: Int, f: (Offset) -> Int) {
        val offset = offsetOf(index)
        val size = f(offset)

        offsets[index + 1] = offset + size // TODO could not write to memory at a cost of an if
        sizes[index] = size
    }

    private fun offsetOf(index: Int): Int {
        if (lastKnownOffset < index) TODO()
        return offsets[index]
    }

    // TODO method: view() : Field.StructureView ?
    fun fragment(size: Int): Structure {
        val bytes = Field.Raw(this, RawEncoding)
        return Fragment(size, bytes)
    }
}

// TODO two arrays: offsets and sizes?
abstract class Layout(private val structure: Structure) {
    protected fun int(encoding: Encoding.OfInt): Field.OfInt = Field.OfInt(structure, encoding)
    protected fun raw(encoding: Encoding.Raw): Field.Raw = Field.Raw(structure, encoding)
    protected fun raw(size: Int): Field.Raw = TODO()
    protected fun <A : Layout> embed(f: (Structure) -> A) = f(structure).also(structure::track)
}

// TODO structure should extend buffer or otherwise field should just see write/read + bind
//   also, having the int/raw/embed methods in the structure
//   would allow creating new layouts without creating new types
open class Field(protected val structure: Structure) {
    // TODO maybe just Raw/Of* fields shouls have an index? would allow the layout to also be a field
    protected val index: Int = structure.bind(this)

    class OfInt(structure: Structure, private val encoding: Encoding.OfInt) : Field(structure) {
        fun write(value: Int) = structure.write(index, encoding, value)
    }

    class Of<A>(structure: Structure, private val encoding: Encoding.Of<A>) : Field(structure) {
        fun write(value: A) = structure.write(index, encoding, value)
    }

    class Raw(structure: Structure, private val encoding: Encoding.Raw) : Field(structure) {
        fun write(value: ByteBuffer) = structure.write(index, encoding, value)
        fun write(value: ByteArray, from: Int = 0, len: Int = value.size - from) {
            structure.write(index, encoding, value, from, len)
        }
    }
}

interface Encoding {
    interface Of<A> : Encoding {
        fun write(buffer: Buffer, offset: Int, value: A): Int
    }

    interface OfInt : Encoding {
        fun write(buffer: Buffer, offset: Int, value: Int): Int
    }

    interface Raw : Of<ByteBuffer> {
        fun write(buffer: Buffer, offset: Int, value: ByteArray, from: Int, len: Int): Int
    }
}