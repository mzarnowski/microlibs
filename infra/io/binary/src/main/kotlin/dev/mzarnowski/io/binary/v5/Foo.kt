package dev.mzarnowski.io.binary.v5

import java.nio.ByteBuffer
import kotlin.math.max

class TlsRecord(structure: Structure) : Layout(structure) {
    val contentType = int(TODO("1 byte"))
    val version = int(TODO("2 bytes"))
    val fragment = raw(FragmentEncoding)
}

object FragmentEncoding : Encoding.Raw {
    private val sizeEncoding: Encoding.OfInt = TODO("two bytes?")

    override fun write(buffer: Buffer, offset: Int, value: ByteArray, from: Int, len: Int): Int {
        val size = max(len, 0xFFFFFF - 1)
        val tagSize = sizeEncoding.write(buffer, offset, size)
        buffer.write(offset + tagSize, value, from, len)
        return tagSize + size
    }

    override fun write(buffer: Buffer, offset: Int, value: ByteBuffer): Int {
        val size = value.limit() - value.position()
        buffer.write(offset, value)
        return size
    }
}

class HandshakeLayout(structure: Structure) : Layout(structure) {
    val typeTag: Encoding.OfInt = TODO()

    object Enc : Encoding.Of<HandshakeMessage.Hello> {
        override fun write(buffer: Buffer, offset: Int, value: HandshakeMessage.Hello): Int = 0
    }

    class ClientHello(structure: Structure) : Layout(structure) {
        val random = embed(::RandomLayout)
        val session = raw(TODO("0..32"))
    }

    class RandomLayout(structure: Structure) : Layout(structure) {
        val timestamp = int(IntEncoding(4))
        val random = raw(TODO("28"))

        fun flush() = Unit
    }
}

object CipherSuiteEncoding : Encoding.Of<CipherSuite> {
    override fun write(buffer: Buffer, offset: Int, value: CipherSuite): Int {
        buffer.write(offset + 0, 0)
        buffer.write(offset + 1, 0)
        TODO("Not yet implemented")
    }
}

sealed class HandshakeMessage {
    object Hello : HandshakeMessage()
}

enum class CipherSuite