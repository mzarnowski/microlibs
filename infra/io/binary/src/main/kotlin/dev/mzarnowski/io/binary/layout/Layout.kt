package dev.mzarnowski.io.binary.layout

import dev.mzarnowski.io.binary.encoding.Encoding

interface Foo {

}

sealed class Buffer {
    class Array(val buffer: ByteArray, val position: Int) : Buffer() {
        fun write(offset: Int, value: Int, encoding: Encoding.OfInt) {
            encoding.write(value, buffer, position + offset)
        }

        fun write(offset: Int, value: ByteArray, start: Int, length: Int, encoding: Encoding.OfBytes) {
//            encoding.write(value, start, buffer, position + offset, length)
        }


    }
}

sealed class Sequence {
    class OfInt(val parent: Foo, index: Int, encoding: Encoding.OfInt) {

    }
}


//
//import dev.mzarnowski.io.binary.encoding.Encoding
//
//interface Structure
//sealed interface Field : Structure {
//    class OfInt(val layout: Layout, val index: Int, val bytes: Int) : Field {
//        fun write(value: Int) = layout.writeInt(index, value, bytes)
//    }
//
//    class Bytes(val layout: Layout, val index: Int, val bytes: Int) : Field {
//        fun write(value: ByteArray, offset: Int) = layout.writeBytes(index, value, offset, bytes)
//    }
//}
//
//interface Layout : Structure {
//    fun writeInt(index: Int, value: Int, bytes: Int)
//    fun writeBytes(index: Int, value: ByteArray, offset: Int, bytes: Int)
//}
//
//sealed interface Sequence : Layout {
//    class OfInt(val layout: Layout, val index: Int, val encoding: Encoding.OfInt) : Sequence {
//        private var count = 0
//        fun append(value: Int) {
//            val offset = count * encoding.size()
//            layout.write(index, value, encoding)
//        }
//    }
//}
//
//class DynamicBlock : Layout {
//    val size: Field.OfInt = TODO()
//
//    fun write
//}
////abstract class Layout {
////    private val fields = mutableListOf<Field>()
////
////    internal abstract fun write(value: Int, offset: Int, encoding: Encoding.OfInt)
////
////    fun offsetOf(value: Int): Int = TODO()
////}
////
////class Root(val bytes: ByteArray, val position: Int) : Layout() {
////    override fun write(value: Int, offset: Int, encoding: Encoding.OfInt) {
////        encoding.write(value, bytes, position + offset)
////    }
////}
////
////class StaticBlock(val parent: Layout, val index: Int, val size: Int) : Layout() {
////    override fun write(value: Int, offset: Int, encoding: Encoding.OfInt) {
////        val position = parent.offsetOf(index)
////        parent.write(value, position + offset, encoding)
////    }
////}