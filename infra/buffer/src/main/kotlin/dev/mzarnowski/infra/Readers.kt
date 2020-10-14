package dev.mzarnowski.infra

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray

internal open class Readers(private val capacity: Int) {
    private val mask: Int = capacity - 1

    // counts amount of readers at each position of the buffer
    private val slots = AtomicIntegerArray(capacity)
    private val count = AtomicInteger()

    @Volatile
    private var first = 0

    private fun increase(offset: Int) = slots.incrementAndGet(offset)
    internal fun decrease(offset: Int) = slots.decrementAndGet(offset)

    fun replace(old: Int, new: Int) {
        increase(new)
        decrease(old)
    }

    fun addClosest(): Int {
        if (count.getAndIncrement() == 0) {
            increase(first)
            return first
        }

        var index = first
        repeat(capacity) {
            val readersAtIndex = slots.getAndUpdate(index) { if (it == 0) 0 else it + 1 }
            if (readersAtIndex > 0) return index
            index = (index + 1) and mask
        }

        slots.getAndIncrement(first)
        return first
    }

    /**
     * @return closest occupied, or [last] if none
     */
    fun findClosest(): Int {
        repeat(capacity) {
            val offset = first
            if (slots[offset] > 0) return offset
            first = (first + 1) and mask
        }

        return (first + 1) and mask
    }

    override fun toString(): String = slots.toString()
}