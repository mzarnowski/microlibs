package dev.mzarnowski.io.binary.v2

interface Structure {
    fun size(): Int
    fun flush(): Unit = Unit // flush comes from the top. This is when heavy computation (e.g. encryption) happens
    fun clear(): Unit = Unit
}

internal interface Member : Structure {
    val layout: Layout
    val index: Int

    fun offset(): Int = layout.offsetOf(index)
}

// a structure which lays out other structures
abstract class Layout : Structure {
    private val fields = mutableListOf<Member>()
    private var offsets = intArrayOf(0)
    private var lastKnownOffset = 0


    fun block(): Layout = DynamicBlock(this, fields.size).also(fields::add)
    fun block(bytes: Int): Layout = StaticBlock(this, fields.size, bytes).also(fields::add)
    fun stream(maxBytes: Int): StreamField = StreamField(this, fields.size, int(maxBytes)).also(fields::add)
    fun int(bytes: Int): IntField = IntField(this, fields.size, bytes).also(fields::add)


    protected fun fieldCount(): Int = fields.size

    override fun clear(): Unit = run { lastKnownOffset = 0 }

    fun offsetOf(index: Int): Int {
        if (lastKnownOffset < index) {
            if (offsets.size < index) {
                offsets = offsets.copyOf(fields.size + 1)
            }

            var offset = offsets[lastKnownOffset]
            repeat(index - lastKnownOffset) {
                offset += fields[lastKnownOffset + it].size()
                offsets[lastKnownOffset + it + 1] = offset
            }
            lastKnownOffset = index
        }

        return offsets[index]
    }

    abstract fun writeInt(offset: Int, value: Int, size: Int)

    //    abstract fun writeLong(offset: Int, value: Long, size: Int)
//    abstract fun writeDouble(offset: Int, value: Double)
//    abstract fun writeInteger(offset: Int, value: BigInteger)
//    abstract fun writeDecimal(offset: Int, value: BigDecimal)
    abstract fun writeBytes(offset: Int, bytes: ByteArray, start: Int, len: Int)
}

// TODO should support byte order
internal class RootLayout(val array: ByteArray) : Layout() {
    override fun size(): Int = offsetOf(fieldCount())

    override fun writeInt(offset: Int, value: Int, size: Int) {
        var shift = 8 * size - 1
        repeat(size) {
            array[offset + it] = (value ushr shift).toByte()
            shift -= 8
        }
    }

    override fun writeBytes(offset: Int, bytes: ByteArray, start: Int, len: Int) {
        System.arraycopy(bytes, start, array, offset, len)
    }
}

class IntField(override val layout: Layout, override val index: Int, val size: Int) : Member {
    override fun size(): Int = size

    fun write(value: Int) = layout.writeInt(offset(), value, size)
}

// TODO size should rather be a structure of (size, Padding)?
internal class ByteArrayField(override val layout: Layout, override val index: Int, val size: Int) : Member {
    override fun size(): Int = size

    fun write(bytes: ByteArray, offset: Int) = layout.writeBytes(offset(), bytes, offset, size)
}


fun main() {
    val bytes = ByteArray(4096)
    val root = RootLayout(bytes)

    val stream = root.stream(2)
    stream.append(byteArrayOf(1, 2, 3))
    stream.append(byteArrayOf(1, 2, 3))
    println(root.size())
}


// [][][][size][X][Y][Z]