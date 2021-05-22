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
        val int = message.int()

        assertThat(int.read()).isEqualTo(0x03040506)
    }

    @Test
    fun `reads long following a stream`() {
        val bytes = byteArrayOf(1, 0, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0)

        val message = ByteMessage().apply {
            readFrom(bytes, 0)
        }

        val stream = message.stream(128)
        val long = message.long()

        assertThat(long.read()).isEqualTo(0x0304050607080910)
    }

    //////////////////////////////////////////
    //                Writes                //
    //////////////////////////////////////////

    @Test
    fun `writes single block`() {
        val bytes = ByteArray(32)
        val message = ByteMessage().apply {
            readFrom(bytes, 0) // TODO rename
        }

        val block = message.block(4)

        block.write(byteArrayOf(5, 4, 3, 2, 1), 1)

        assertThat(bytes.take(4)).isEqualTo(byteArrayOf(4, 3, 2, 1).toList())
    }

    @Test
    fun `writes consecutive block`() {
        val bytes = ByteArray(32)
        val message = ByteMessage().apply {
            readFrom(bytes, 0) // TODO rename
        }

        val firstBlock = message.block(4)
        val secondBlock = message.block(3)

        firstBlock.write(byteArrayOf(5, 4, 3, 2, 1), 1)
        secondBlock.write(byteArrayOf(9, 8, 7, 2, 1))

        assertThat(bytes.drop(4).take(3)).isEqualTo(byteArrayOf(9, 8, 7).toList())
    }

    @Test
    fun `writes stream`() {
        val bytes = ByteArray(32)
        val message = ByteMessage().apply {
            readFrom(bytes, 0) // TODO rename
        }

        val stream = message.stream(128)

        stream.append(byteArrayOf(0x01, 0x02, 0x03))
        stream.flush()

        assertThat(bytes.take(4)).isEqualTo(byteArrayOf(0x03, 0x01, 0x02, 0x03).toList())
    }

    @Test
    fun `writes byte`() {
        val bytes = ByteArray(32)
        val message = ByteMessage().apply {
            readFrom(bytes, 0) // TODO rename
        }

        val byte = message.byte()

        byte.write(0x6f)
        assertThat(bytes.take(4)).isEqualTo(byteArrayOf(0x6f, 0, 0, 0).toList())
    }

    @Test
    fun `writes short`() {
        val bytes = ByteArray(32)
        val message = ByteMessage().apply {
            readFrom(bytes, 0) // TODO rename
        }

        val byte = message.short()

        byte.write(0x6f5e)
        assertThat(bytes.take(4)).isEqualTo(byteArrayOf(0x6f, 0x5e, 0, 0).toList())
    }

    @Test
    fun `writes int`() {
        val bytes = ByteArray(32)
        val message = ByteMessage().apply {
            readFrom(bytes, 0) // TODO rename
        }

        val byte = message.int()

        byte.write(0x6f5e4d3c)
        assertThat(bytes.take(4)).isEqualTo(byteArrayOf(0x6f, 0x5e, 0x4d, 0x3c).toList())
    }

    @Test
    fun `writes long`() {
        val bytes = ByteArray(32)
        val message = ByteMessage().apply {
            readFrom(bytes, 0) // TODO rename
        }

        val byte = message.long()

        byte.write(0x0102030405060708)
        assertThat(bytes.take(8)).isEqualTo(byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08).toList())
    }
}