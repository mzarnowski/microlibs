package dev.mzarnowski.infra

typealias Factory = (Int, Int, Bench) -> BufferAdapter

enum class AdapterFactory(val factory: Factory) {
    MyBuffer(::MyBufferAdapter),
    LmaxBuffer(::LmaxAdapter),
    ;

    fun create(capacity: Int, batchSize: Int, c: Bench): BufferAdapter = factory(capacity, batchSize, c)
}