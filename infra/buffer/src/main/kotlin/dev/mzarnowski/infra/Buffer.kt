package dev.mzarnowski.infra

class Buffer<A>(private val values: Array<A>) {
    val capacity: Int = values.size
    val mask: Int = capacity - 1

    val writer = Writer(this)
    internal val readers: Readers = Readers(capacity)

    fun write(offset: Int, value: A) {
        values[offset] = value
    }

    fun read(offset: Int): A {
        return values[offset]
    }

    fun reader(): Reader<A> {
        val closest = readers.addClosest()
        return Reader(this, closest)
    }

    companion object {
        inline operator fun <reified A> invoke(capacity: Int): Buffer<A> {
            require(Integer.bitCount(capacity) == 1) { "Capacity must be a power of 2" }
            require(capacity > 1) { "Buffer must be able to store at least two elements" }
            val array = arrayOfNulls<A>(capacity) as Array<A>
            return Buffer(array)
        }
    }
}