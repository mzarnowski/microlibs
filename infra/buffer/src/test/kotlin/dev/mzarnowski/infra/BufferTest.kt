package dev.mzarnowski.infra

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BufferTest {
    @Test
    fun writer_can_claim_entire_buffer_immediately() {
        buffer(Capacity).apply {
            val available = writer.claim(0)

            assertThat(available).isEqualTo(InitialCapacity)
        }
    }

    @Test
    fun writer_can_release_without_claiming_or_writing() {
        val skipped = 2
        buffer(Capacity).apply {
            writer.release(skipped)

            val available = writer.claim(0)
            assertThat(available).isEqualTo(InitialCapacity - skipped)
        }
    }

    @Test
    fun writer_availability_is_reduced_by_subsequent_releases() {
        buffer(Capacity).apply {
            writer.release(1)
            writer.release(2)

            val available = writer.claim(0)
            assertThat(available).isEqualTo(InitialCapacity - 3)
        }
    }

    @Test
    fun writer_does_not_guard_against_releasing_over_availability() {
        buffer(Capacity).apply {
            writer.release(Capacity + 3)

            // even though we release more that the buffer capacity,
            // it is not considered "full" and subsequent claims might
            // cause the data to be overwritten
            val available = writer.claim(0)
            assertThat(available).isNotEqualTo(0)
        }
    }

    @Test
    fun reading_value_increases_writer_availability() {
        buffer(Capacity, 1, 2).apply {
            val reader = reader()

            val before = writer.claim(1)
            val read = reader.readValues(2)
            val after = writer.claim(Capacity)

            assertThat(read).containsExactly(1, 2)
            assertThat(after - before).isEqualTo(2)
        }
    }

    @Test
    fun reader_cannot_claim_anything_initially() {
        buffer(Capacity).apply {
            val reader = reader()

            val claimed = reader.claim(1)

            assertThat(claimed).isEqualTo(0)
        }
    }

    @Test
    fun reader_cannot_claim_without_writer_releasing() {
        buffer(Capacity).apply {
            val reader = reader()

            writer.claim(1)
            writer.write(0, 42)

            val claimed = reader.claim(1)

            assertThat(claimed).isEqualTo(0)
        }
    }

    @Test
    fun reader_can_claim_after_writer_releases() {
        buffer(Capacity).apply {
            val reader = reader()

            writer.release(1)

            val claimed = reader.claim(1)

            assertThat(claimed).isEqualTo(1)
        }
    }

    @Test
    fun disposing_lagging_reader_unblocks_writer() {
        buffer(Capacity).apply {
            val r1 = reader()
            val r2 = reader()

            writer.release(InitialCapacity)
            r1.release(4)
            assertThat(writer.claim(1)).isEqualTo(0)

            // dispose lagging reader to unblock writing
            r2.dispose()

            assertThat(writer.claim(Capacity)).isEqualTo(InitialCapacity - r1.claim(Capacity))
        }
    }

    @Test
    fun reader_reads_all_available_elements() {
        buffer(Capacity).apply {
            val reader = reader()

            val available = writer.claim(4)
            if (available < 4) fail("Writer cannot write 4 values")
            repeat(4) { writer.write(it, it) }
            writer.release(4)

            val read = reader.readAll()
            assertThat(read).containsExactly(0, 1, 2, 3)
            assertThat(reader.claim(Int.MAX_VALUE)).isEqualTo(0)
        }
    }

    @Test
    fun reader_reads_wrapped_range() {
        buffer(Capacity).apply {
            val reader = reader()
            var index = 0
            fun next(): Int = (index++ and InitialCapacity)

            writer.claim(Int.MAX_VALUE).also { available ->
                repeat(available) { writer.write(it, next()) }
                writer.release(available)
            }

            reader.claim(3).also { available ->
                if (available < 3) fail("Reader cannot read 3 values")
                repeat(3, reader::read)
                reader.release(3)
            }

            writer.claim(Int.MAX_VALUE).also { available ->
                repeat(available) { writer.write(it, next()) }
                writer.release(available)
            }

            val read = reader.readAll()
            assertThat(read).containsExactly(3, 4, 5, 6, 7, 0, 1)
            assertThat(reader.claim(Int.MAX_VALUE)).isEqualTo(0)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 4, 8, 16, 32])
    fun readers_consume_written_values(readerCount: Int) {
        val amount = 3
        val expected = (0 until amount).toList()

        buffer(Capacity).apply {
            val readers = (0 until readerCount).map { Receiver(amount, reader(), it) }
            readers.forEach { it.start() }

            val available = writer.claim(amount)
            if (available < amount) fail("Initially, reader should be able to claim at least 3 elements")

            for (offset in expected.indices) {
                writer.write(offset, expected[offset])
            }
            writer.release(amount) // don't over-release by using `available`

            while (readers.any { it.received < amount }) {
                if (Thread.currentThread().isInterrupted) fail("Interrupted")
                Thread.onSpinWait()
            }

            readers.forEach { assertThat(it.received).isEqualTo(amount) }
        }
    }

    private companion object {
        const val Capacity = 8
        const val InitialCapacity = Capacity - 1

        fun buffer(size: Int, vararg values: Int): Buffer<Int> {
            return Buffer<Int>(size).apply {
                val available = writer.claim(values.size)
                if (available < values.size) fail("Could not initialize buffer of capacity [$Capacity] with values: $values")
                for (i in values.indices) {
                    writer.write(i, values[i])
                }

                writer.release(values.size)
            }
        }

        fun <A> Reader<A>.readValues(n: Int): List<A> {
            val available = claim(n)
            if (available < n) fail("Could not read $n values. Only $available is available")
            return (0 until n).map(::read).also {
                release(available)
            }
        }
    }
}