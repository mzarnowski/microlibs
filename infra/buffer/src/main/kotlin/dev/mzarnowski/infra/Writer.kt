package dev.mzarnowski.infra

class Writer<A> internal constructor(val buffer: Buffer<A>) {
    private val mask = buffer.mask
    private val length = buffer.capacity

    @Volatile
    private var first = 0

    private var available = length - 1

    fun position(): Int = first

    fun claim(amount: Int): Int {
        if (available < amount) {
            // a slot just before the reader.
            val lastReader = buffer.readers.findClosest()
            available = (lastReader - first - 1) and mask
        }

        return available
    }

    fun release(n: Int) {
        first = (first + n) and mask
        available -= n
    }

    fun write(offset: Int, value: A) {
        buffer.write((first + offset) and mask, value)
    }
}
