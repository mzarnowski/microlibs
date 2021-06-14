//package dev.mzarnowski.io.binary.encoding
//
//class IntEncoding internal constructor(val bytes: Int) : Encoding.OfInt {
//    override fun write(value: Int, buffer: ByteArray, offset: Int) {
//        var shift = 8 * (bytes - 1)
//        repeat(bytes) {
//            buffer[offset + it] = (value ushr shift).toByte()
//            shift -= 8
//        }
//    }
//
//    override fun read(buffer: ByteArray, offset: Int): Int {
//        var value = 0
//        var shift = 8 * (bytes - 1)
//        repeat(bytes) {
//            value += buffer[offset + it].toInt() shl shift
//            shift -= 8
//        }
//        return value
//    }
//
//    companion object {
//        val OneByte = IntEncoding(1)
//        val TwoBytes = IntEncoding(2)
//        val ThreeBytes = IntEncoding(3)
//        val FourBytes = IntEncoding(4)
//    }
//}