package dev.mzarnowski.infra;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.infra.ThreadParams;

import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 6, time = 15)
@Fork(value = 1, warmups = 1)
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BufferReadWrite {
    @Param
    AdapterFactory buffer;
    public BufferAdapter adapter;

    @Param({"4096"})
    int capacity;

    int maxBatch = 32;

    @Setup(Level.Iteration)
    public void setUp(Control c, ThreadParams tp) {
        var bench = new Bench(tp, c);
        adapter = buffer.create(capacity, maxBatch, bench);
    }

    @TearDown(Level.Iteration)
    public void verify() {
        adapter.verify();
    }

    @Benchmark
    @Group("rw")
    @GroupThreads(1)
    public void batchWrite(WriterCounters counters) {
        adapter.getWriter().write(counters);
    }

    @Benchmark
    @Group("rw")
    @GroupThreads(1)
    public void batchRead(Reader reader, ReaderCounters counters) {
        reader.reader.read(counters);
    }

    @State(Scope.Thread)
    public static class Reader {
        ReaderAdapter reader;

        @Setup(Level.Iteration)
        public void setUp(BufferReadWrite benchmark, ThreadParams tp) {
            reader = benchmark.adapter.reader(tp);
        }
    }
}

