package dev.mzarnowski.io.binary.encoding

import kotlin.math.min

interface Encoding {
    fun size(): Int

    class OfInt(val bytes: Int) : Encoding {
        fun write(value: Int, buffer: ByteArray, offset: Int) {
            buffer[offset] = value.toByte()
        }

        fun read(buffer: ByteArray, offset: Int): Int = buffer[offset].toInt() and 0xFF

        override fun size(): Int = bytes
    }

    class OfBytes(val size: Int) : Encoding {
        fun write(values: ByteArray, from: Int = 0, length: Int = values.size - from, buffer: ByteArray, offset: Int) {
            val size = min(size, length)
            System.arraycopy(values, from, buffer, offset, size)
        }

        fun read(buffer: ByteArray, offset: Int): ByteArray = buffer.copyOfRange(offset, offset + size)

        override fun size(): Int = size
    }

    interface Of<A> : Encoding {
        fun write(value: A, buffer: ByteArray, offset: Int)
        fun read(buffer: ByteArray, offset: Int): A
    }
}

interface Sequence : Encoding {
    class OfInt(private val encoding: Encoding.OfInt, limit: Int) : Sequence {
        private val sizeEncoding = Encoding.OfInt(limit)

        fun write(values: IntArray, buffer: ByteArray, offset: Int) {
            sizeEncoding.write(values.size, buffer, offset)
            values.indices.forEach {
                val position = offset + sizeEncoding.size() + it * encoding.size()
                encoding.write(values[it], buffer, position)
            }
        }

        override fun size(): Int {
            TODO("Not yet implemented")
        }
    }

    class OfBytes(val limit: Int) {
        private val sizeEncoding = Encoding.OfInt(limit)

        fun write(values: ByteArray, from: Int = 0, length: Int = values.size - from, buffer: ByteArray, offset: Int) {
            val size = min(limit, length - sizeEncoding.bytes)
            sizeEncoding.write(size, buffer, offset)
            System.arraycopy(values, from, buffer, offset + sizeEncoding.bytes, size)
        }
    }

    class Of<A>(private val encoding: Encoding.Of<A>, limit: Int) {
        private val sizeEncoding = Encoding.OfInt(limit)

        fun write(values: Array<A>, buffer: ByteArray, offset: Int) {
            sizeEncoding.write(values.size, buffer, offset)
            values.indices.forEach {
                val position = offset + sizeEncoding.size() + it * encoding.size()
                encoding.write(values[it], buffer, position)
            }
        }

        fun write(values: Collection<A>, buffer: ByteArray, offset: Int) {
            sizeEncoding.write(values.size, buffer, offset)
            values.withIndex().forEach {
                val position = offset + sizeEncoding.size() + it.index * encoding.size()
                encoding.write(it.value, buffer, position)
            }
        }
    }
}

interface Layout {
    interface Dynamic : Layout
    interface Static : Layout
}