package dev.mzarnowski.io.binary.v3

interface Encoding {
    interface Of<A> : Encoding {
        fun write(output: ByteArray, offset: Int, value: A): Int
    }

    // silent assumption we encode number of bytes as size. This should be selectable
    interface SeqOf<A> : Encoding {
        fun write(output: ByteArray, offset: Int, values: Array<A>): Int
        fun write(output: ByteArray, offset: Int, values: Collection<A>): Int
    }


    class OfInt(internal val bytes: Int) : Encoding {
        fun write(output: ByteArray, offset: Int, value: Int): Int {
            // TODO()
            return bytes
        }
    }

    open class OfRefOnInt<A>(val next: OfInt, val f: (A) -> Int) : Of<A> {
        constructor(bytes: Int, f: (A) -> Int) : this(OfInt(bytes), f)

        final override fun write(output: ByteArray, offset: Int, value: A): Int {
            return next.write(output, offset, f(value))
        }
    }

    interface OfBytes : Encoding {
        fun write(output: ByteArray, offset: Int, value: ByteArray, from: Int, len: Int): Int
    }
}

fun interface Padding {
    fun pad(output: ByteArray, offset: Int, len: Int)

    object None : Padding {
        override fun pad(output: ByteArray, offset: Int, len: Int) = Unit
    }
}