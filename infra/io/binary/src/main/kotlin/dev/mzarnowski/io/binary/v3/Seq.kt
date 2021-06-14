package dev.mzarnowski.io.binary.v3

import java.util.*
import java.util.function.IntConsumer

interface SequenceEncoding : Encoding {
    class Of<A>(val encoding: Encoding.Of<A>, val size: Encoding.OfInt, val sizeInBytes: Boolean) : SequenceEncoding {
        fun write(output: ByteArray, offset: Int, values: Array<A>): Int {
            var elementOffset = offset + size.bytes
            values.forEach {
                elementOffset += encoding.write(output, elementOffset, it)
            }

            if (sizeInBytes) {
                size.write(output, offset, elementOffset - offset - size.bytes)
            } else {
                size.write(output, offset, values.size)
            }

            return elementOffset - offset // include size field
        }

        fun write(output: ByteArray, offset: Int, values: Collection<A>): Int {
            var elementOffset = offset + size.bytes
            values.forEach {
                elementOffset += encoding.write(output, elementOffset, it)
            }

            if (sizeInBytes) {
                size.write(output, offset, elementOffset - offset - size.bytes)
            } else {
                size.write(output, offset, values.size)
            }

            return elementOffset - offset // include size field
        }

        fun write(output: ByteArray, offset: Int, values: Iterator<A>): Int {
            var count = 0
            var elementOffset = offset + size.bytes
            values.forEach {
                elementOffset += encoding.write(output, elementOffset, it)
                count += 1
            }

            if (sizeInBytes) {
                size.write(output, offset, elementOffset - offset - size.bytes)
            } else {
                size.write(output, offset, count)
            }

            return elementOffset - offset // include size field
        }
    }

    class  OfInt(val encoding: Encoding.OfInt, val size: Encoding.OfInt, val sizeInBytes: Boolean) : SequenceEncoding {
        fun write(output: ByteArray, offset: Int, values: IntArray): Int {
            var elementOffset = offset + size.bytes
            values.forEach {
                elementOffset += encoding.write(output, elementOffset, it)
            }

            if (sizeInBytes) {
                size.write(output, offset, elementOffset - offset - size.bytes)
            } else {
                size.write(output, offset, values.size)
            }

            return elementOffset - offset // include size field
        }

        fun write(output: ByteArray, offset: Int, values: PrimitiveIterator.OfInt): Int {
            var count = 0
            var elementOffset = offset + size.bytes
            values.forEachRemaining(IntConsumer { it: Int ->
                elementOffset += encoding.write(output, elementOffset, it)
                count += 1
            })

            if (sizeInBytes) {
                size.write(output, offset, elementOffset - offset - size.bytes)
            } else {
                size.write(output, offset, count)
            }

            return elementOffset - offset // include size field
        }
    }
}