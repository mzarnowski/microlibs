package dev.mzarnowski.infra

import com.lmax.disruptor.*
import com.lmax.disruptor.util.ThreadHints
import org.openjdk.jmh.infra.Control
import org.openjdk.jmh.infra.ThreadParams
import kotlin.system.exitProcess

class LmaxAdapter(capacity: Int, private val batch: Int, private val bench: Bench) : BufferAdapter {
    private val wait = ControlledWait(bench.c)
    private val buffer = RingBuffer.createSingleProducer(::LmaxCell, capacity, wait)
    private val readers = (1..bench.readerThreads).map { LmaxReader(buffer, batch, bench.c) }

    override val writer = LmaxWriter(buffer, batch, bench.c)
    override fun reader(tp: ThreadParams) = readers[tp.threadIndex % readers.size]

    override fun verify() = run { readers.forEach { verify(it) } }
    override fun update(readerCounters: ReaderCounters) {
        readerCounters.batchReadMissed = wait.missed / bench.readerThreads
    }

    private fun verify(reader: LmaxReader) {
        val remaining = (buffer.cursor - reader.seq.get())
        val difference = writer.written - (reader.read + remaining)
        if (difference != 0L) {
            print("${reader.read + remaining} <> ${writer.written} at $difference ")
            exitProcess(1)
        }
    }
}

class LmaxWriter(val buffer: RingBuffer<LmaxCell>, val batch: Int, val c: Control) : WriterAdapter() {
    override fun write(counters: WriterCounters) {
        val lo = buffer.cursor + 1
        var hi = 0L
        while (hi < batch) {
            counters.batchWriteMissed++
            if (c.stopMeasurement) return

            try {
                hi = buffer.tryNext(batch)
            } catch (e: InsufficientCapacityException) {
                continue
            }
        }

        try {
            for (i in lo..hi) {
                buffer[i].value = written++
            }
        } finally {
            buffer.publish(lo, hi)
        }
    }
}

class LmaxReader(val buffer: RingBuffer<LmaxCell>, val batch: Int, val c: Control) : ReaderAdapter() {
    val seq = Sequence().also { buffer.addGatingSequences(it) }
    val barrier = buffer.newBarrier()

    override fun read(counters: ReaderCounters) {
        val lo = seq.get() + 1
        val hi = lo + batch

        while (true) {
            if (c.stopMeasurement) return

            try {
                barrier.waitFor(hi)
                break
            } catch (e: TimeoutException) {
                // don't count a miss, it is taken care of by the WaitStrategy
                return
            }
        }

        for (i in lo..hi) {
            if (buffer[i].value == read) read++
            else {
                println("buffer[$i].value => ${buffer[i].value}")
                System.exit(1)
            }
        }

        seq.set(hi)
    }
}

private class ControlledWait(val c: Control) : WaitStrategy {
    var missed = 0

    override fun waitFor(seq: Long, cursor: Sequence?, dependent: Sequence, barrier: SequenceBarrier): Long {
        var available: Long
        while (dependent.get().also { available = it } < seq) {
            missed++
            if (c.stopMeasurement) throw TimeoutException.INSTANCE
            barrier.checkAlert()
            ThreadHints.onSpinWait()
        }
        return available
    }

    override fun signalAllWhenBlocking() {}
}
