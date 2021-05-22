package dev.mzarnowski.io.binary

fun interface Padding {
    fun pad(bytes: ByteArray, offset: Int, size: Int)

    object None : Padding {
        override fun pad(bytes: ByteArray, offset: Int, size: Int) = Unit
    }
}