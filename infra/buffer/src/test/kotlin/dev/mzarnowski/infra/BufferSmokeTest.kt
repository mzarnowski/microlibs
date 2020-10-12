package dev.mzarnowski.infra

import dev.mzarnowski.infra.BufferSmokeTest.Companion.Expected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.math.min


class BufferSmokeTest {
    companion object {
        const val Capacity = 1.shl(16)
        const val Expected = 100_000_000
        const val Receivers = 5 // higher than 5-6 might cause high contention and hence slow tests
    }

    @Test
    fun send_all_messages_in_order() {
        val buffer = Buffer<Int>(Capacity)

        val sender = Sender(buffer.writer)
        val receivers = (0 until Receivers).map { Receiver(Expected, buffer.reader(), it) }

        sender.start()
        receivers.forEach { it.start() }

        sender.join()
        receivers.forEach { it.join() }

        println("${sender.name} sent ${sender.sent}")
        receivers.forEach { println("${it.name} got ${it.received}") }

        receivers.forEach {
            assertThat(it.received)
                .withFailMessage("${it.name} didn't receive all of the messages")
                .isEqualTo(sender.sent)
        }

    }
}

class Sender(val writer: Writer<Int>) : Thread("sender") {
    val batch = 73
    var sent = 0

    override fun run() {
        while (sent < Expected) {
            if (currentThread().isInterrupted) return

            val available = min(Expected - sent, writer.claim(batch))
            for (offset in 0 until available) {
                writer.write(offset, sent++)
            }
            writer.release(available)
        }
    }
}

class Receiver<A>(val expected: Int, val reader: Reader<A>, id: Int) : Thread("receiver-$id") {
    val batch = 51
    var received = 0

    override fun run() {
        while (received < expected) {
            if (currentThread().isInterrupted) return

            val available = reader.claim(batch)
            for (offset in 0 until available) {
                val read = reader.read(offset)
                if (received == read) received++
                else throw Exception("$name received out of order message: $read. Expected $received")
            }
            reader.release(available)
        }
    }
}