package dev.mzarnowski.infra

import org.openjdk.jmh.infra.Control
import org.openjdk.jmh.infra.ThreadParams

class Bench(tp: ThreadParams, val c: Control) {
    val readerThreads = tp.threadCount - 1
}