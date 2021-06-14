package dev.mzarnowski.io.binary.v4

sealed interface Layout {
    fun flush()
}

sealed class Field(private val parent: Structure, private val index: Int) : Layout {
    protected fun write(f: (Buffer, Int) -> Unit) = parent.write(index, f)
    override fun flush() = Unit

    class OfInt(parent: Structure, index: Int, val encoding: Encoding.OfInt) : Field(parent, index) {
        fun write(value: Int) = write { buffer, offset -> encoding.write(buffer, offset, value) }
    }

    class Of<A>(parent: Structure, index: Int, val encoding: Encoding.Of<A>) : Field(parent, index) {
        fun write(value: A) = write { buffer, offset -> encoding.write(buffer, offset, value) }
    }

    class OfSeq<A>(parent: Structure, index: Int, val encoding: Encoding.OfSeq<A>) : Field(parent, index) {
        fun write(value: Array<A>) = write { buffer, offset -> encoding.write(buffer, offset, value) }
        fun write(value: Iterable<A>) = write { buffer, offset -> encoding.write(buffer, offset, value) }
        fun write(value: Iterator<A>) = write { buffer, offset -> encoding.write(buffer, offset, value) }
    }
}

sealed class Structure : Layout {
    private val fields = mutableListOf<Layout>()
    private var offsets = intArrayOf(0)

    fun int(): Field.OfInt = fieldOf { structure, index -> Field.OfInt(structure, index, TODO()) }
    private fun <A : Layout> fieldOf(f: (Structure, Int) -> A): A = f(this, fields.size).also(fields::add)

    // called by fields
    internal fun write(index: Int, f: (Buffer, Int) -> Unit) {
        TODO()
    }

    // called by embedded structures
    internal abstract fun write(index: Int, offset: Int, f: (Buffer, Int) -> Unit): Int

    internal fun offsetOf(index: Int): Int = TODO()
}

abstract class Block(val parent: Structure, val index: Int) : Structure() {
    internal fun write(index: Int, f: (Buffer, Int) -> Int): Int {
        val fieldOffset = offsetOf(index)
        var offset = fieldOffset
        var structure: Structure = this
        while (structure is Block) {
            offset += parent.offsetOf(structure.index)
            structure = parent
        }

        if (structure is Block) {
            println()
        }
        TODO()
    }

    final override fun write(index: Int, offset: Int, f: (Buffer, Int) -> Unit): Int {
        return parent.write(this.index, offsetOf(index) + offset, f)
    }

    class Static(parent: Structure, index: Int, val size: Int) : Block(parent, index) {
        override fun flush() = Unit
    }

    class Dynamic(parent: Structure, index: Int, val limit: Int) : Block(parent, index) {
        private val size: Field.OfInt = int()
        override fun flush() = TODO()
    }
}