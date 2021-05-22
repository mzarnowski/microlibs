package dev.mzarnowski.io.binary

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ByteMessageFieldTest {
    @Test
    fun `reads single block`() {
        val bytes = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val block = message.block(8)

        assertThat(block.readBlock().toList()).isEqualTo(bytes.take(8))
    }

    @Test
    fun `reads consecutive blocks`() {
        val bytes = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val first = message.block(3)
        val second = message.block(4)

        assertThat(first.readBlock().toList()).isEqualTo(bytes.take(3))
        assertThat(second.readBlock().toList()).isEqualTo(bytes.drop(3).take(4))
    }

    @Test
    fun `reads stream`() {
        val bytes = byteArrayOf(3, 1, 2, 3, 4, 5)
        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val stream = message.stream(128)
        assertThat(stream.readBlock()).isEqualTo(byteArrayOf(1, 2, 3))
    }

    @Test
    fun `reads byte`() {
        val bytes = byteArrayOf(0x64, 0x65)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val byte = message.byte()

        assertThat(byte.read()).isEqualTo(0x64)
    }

    @Test
    fun `reads short`() {
        val bytes = byteArrayOf(0x64, 0x65)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val byte = message.short()

        assertThat(byte.read()).isEqualTo(0x6465)
    }

    @Test
    fun `reads int`() {
        val bytes = byteArrayOf(0x64, 0x65, 0x66, 0x67)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val byte = message.int()

        assertThat(byte.read()).isEqualTo(0x64656667)
    }

    @Test
    fun `reads long`() {
        val bytes = byteArrayOf(0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x70, 0x71)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val byte = message.long()

        assertThat(byte.read()).isEqualTo(0x6465666768697071)
    }

    @Test
    fun `reads block following a stream`() {
        val bytes = byteArrayOf(1, 0, 3, 4, 5, 6, 0)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val stream = message.stream(128)
        val block = message.block(4)

        assertThat(block.readBlock()).isEqualTo(byteArrayOf(3, 4, 5, 6))
    }

    @Test
    fun `reads stream following a stream`() {
        val bytes = byteArrayOf(1, 0, 2, 4, 5, 6, 0)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val firstStream = message.stream(128)
        val secondStream = message.stream(128)

        assertThat(secondStream.readBlock()).isEqualTo(byteArrayOf(4, 5))
    }

    @Test
    fun `reads integer following a stream`() {
        val bytes = byteArrayOf(1, 0, 0x03, 0x04, 0x05, 0x06, 0)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val stream = message.stream(128)
        val block = message.int()

        assertThat(block.read()).isEqualTo(0x03040506)
    }

    @Test
    fun `reads long following a stream`() {
        val bytes = byteArrayOf(1, 0, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val stream = message.stream(128)
        val block = message.long()

        assertThat(block.read()).isEqualTo(0x0304050607080910)
    }
}