package dev.mzarnowski.infra

import org.openjdk.jmh.infra.Control
import org.openjdk.jmh.infra.ThreadParams
import kotlin.math.min
import kotlin.system.exitProcess

class MyBufferAdapter(capacity: Int, private val batch: Int, private val bench: Bench) : BufferAdapter {
    private val buffer = Buffer<Int>(capacity)
    private val readers = (1..bench.readerThreads).map { MyBufferReader(buffer, batch, bench.c) }

    override val writer = MyBufferWriter(buffer, batch, bench.c)
    override fun reader(tp: ThreadParams) = readers[tp.threadIndex % readers.size]

    override fun verify() = run { readers.forEach { verify(it) } }

    private fun verify(reader: MyBufferReader) {
        val remaining = (writer.writer.position() - reader.reader.position()) and buffer.mask
        val difference = writer.written - (reader.read + remaining)
        if (difference == 0) {
            print("${reader.read + remaining} == ${writer.written} ")
        } else {
            print("${reader.read + remaining} <> ${writer.written} ")
            exitProcess(1)
        }
    }

    override fun update(readerCounters: ReaderCounters) {}
}

class MyBufferWriter(val buffer: Buffer<Int>, private val batch: Int, private val c: Control) : WriterAdapter() {
    internal val writer: Writer<Int> = buffer.writer

    override fun write(counters: WriterCounters) {
        var available = min(batch, writer.claim(batch))
        while (available < batch) {
            counters.batchWriteMissed += 1
            if (c.stopMeasurement) return
            available = min(batch, writer.claim(batch))
        }

        for (offset in 0 until available) {
            writer.write(offset, written++)
        }

        writer.release(available)
    }

}

class MyBufferReader(val buffer: Buffer<Int>, private val batch: Int, private val c: Control) : ReaderAdapter() {
    internal val reader: Reader<Int> = buffer.reader()

    override fun read(counters: ReaderCounters) {
        var available = min(batch, reader.claim(batch))
        while (available < batch) {
            counters.batchReadMissed += 1
            if (c.stopMeasurement) return
            available = min(batch, reader.claim(batch))
        }

        for (offset in 0 until available) {
            val value = reader.read(offset)
            if (value == read) read++
        }

        reader.release(available)
    }
}