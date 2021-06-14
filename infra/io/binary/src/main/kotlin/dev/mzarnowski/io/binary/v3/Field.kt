//package dev.mzarnowski.io.binary.v3
//
//sealed interface Field {
//    class OfInt(val parent: Layout, val index: Int, val encoding: Encoding.OfInt) : Field {
//        fun write(value: Int) = parent.write { buffer, offset -> encoding.write(buffer, offset, value) }
//    }
//
//    class OfBytes(val parent: Layout, val index: Int, val encoding: Encoding.OfBytes) : Field {
//        fun write(value: ByteArray, from: Int = 0, length: Int = value.size - from) {
//            parent.write { buffer, offset -> encoding.write(buffer, offset, value, from, length) }
//        }
//    }
//
//    open class Of<A>(val parent: Layout, val index: Int, val encoding: Encoding.Of<A>) : Field {
//        fun write(value: A) = parent.write { buffer, offset -> encoding.write(buffer, offset, value) }
//    }
//
//    class OfSeq<A>(val parent: Layout, val index: Int, val encoding: SequenceEncoding.Of<A>) : Field {
//        fun write(values: Array<A>) = parent.write(index, 0, values, encoding)
//        fun write(values: Collection<A>) = parent.write(index, 0, values, encoding)
//        fun write(values: Iterator<A>) = parent.write(index, 0, values, encoding)
//    }
//}