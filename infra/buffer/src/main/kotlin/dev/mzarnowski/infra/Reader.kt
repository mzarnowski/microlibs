package dev.mzarnowski.infra

class Reader<A> internal constructor(private val buffer: Buffer<A>, private var first: Int) {
    private val mask = buffer.mask
    private val writer = buffer.writer

    private var available = 0

    fun position(): Int = first

    fun claim(amount: Int): Int {
        if (available < amount) {
            available = (writer.position() - first) and mask
        }

        return available
    }

    fun read(offset: Int): A = buffer.read((first + offset) and mask)

    fun release(n: Int) {
        val to = (first + n) and mask

        // should be done ASAP to unblock writer
        buffer.readers.replace(first, to)
        available -= n
        first = to
    }

    fun dispose() {
        buffer.readers.decrease(first)
    }
}
