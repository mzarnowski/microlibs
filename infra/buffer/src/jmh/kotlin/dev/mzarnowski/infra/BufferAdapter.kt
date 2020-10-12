package dev.mzarnowski.infra

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.ThreadParams


interface BufferAdapter {
    val writer: WriterAdapter
    fun reader(tp: ThreadParams): ReaderAdapter

    fun verify()
    fun update(readerCounters: ReaderCounters)
}

abstract class WriterAdapter {
    var written: Int = 0
    abstract fun write(counters: WriterCounters)
}

abstract class ReaderAdapter {
    var read: Int = 0
    abstract fun read(counters: ReaderCounters)
}

@AuxCounters
@State(Scope.Thread)
open class WriterCounters {
    @JvmField
    var batchWriteMissed: Int = 0

    @Setup(Level.Iteration)
    fun setUp() {
        batchWriteMissed = 0
    }
}

@AuxCounters
@State(Scope.Thread)
open class ReaderCounters {
    @JvmField
    var batchReadMissed: Int = 0

    @Setup(Level.Iteration)
    fun setUp() {
        batchReadMissed = 0
    }

    @TearDown(Level.Iteration)
    fun update(bench: BufferReadWrite) {
        bench.adapter.update(this)
    }
}