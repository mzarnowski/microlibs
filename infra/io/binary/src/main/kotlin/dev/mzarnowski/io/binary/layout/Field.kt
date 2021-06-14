package dev.mzarnowski.io.binary.layout

import dev.mzarnowski.io.binary.encoding.Encoding

//abstract class Field(protected val layout: Layout, protected val index: Int)
//
//internal sealed interface BinaryField {
//    fun size(): Int
//}
//
//abstract class Block(layout: Layout, index: Int) : BinaryField, Field(layout, index)
//abstract class OfInt(layout: Layout, index: Int, encoding: Encoding.OfInt) : Field(layout, index)