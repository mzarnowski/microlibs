package dev.mzarnowski.io.binary.v4


interface Encoding {
    interface OfInt : Encoding {
        fun write(buffer: Buffer, offset: Int, value: Int): Int
    }

    interface OfBytes : Encoding {
        fun write(buffer: Buffer, offset: Int, value: ByteArray, from: Int = 0, length: Int = value.size - from): Int
    }

    interface Of<A> : Encoding {
        fun write(output: Buffer, offset: Int, value: A): Int
    }

    interface OfSeq<A> : Encoding {
        fun write(output: Buffer, offset: Int, value: Array<A>): Int
        fun write(output: Buffer, offset: Int, value: Iterable<A>): Int
        fun write(output: Buffer, offset: Int, value: Iterator<A>): Int
    }
}