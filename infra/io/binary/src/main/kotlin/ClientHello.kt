//package dev.mzarnowski.tls
//
//import dev.mzarnowski.io.binary.ByteMessage
//import java.math.BigInteger
//import javax.crypto.Cipher
//
//class Record {
//    private val layout = ByteMessage()
//
//    // record
//    private val type = layout.byte()
//    private val protocol = layout.short()
//    private val stream = layout.stream(0xFFFF - 1)
//
//    // handshake
//    private val handshake = Handshake(layout.stream(0xFFFFFF - 1))
//    private val handshakeType = stream.byte()
//}
//
//open class Handshake : Layout() {
//    private val type = integer(1)
//    private val payload = stream(0xFFFFFF - 1)
//
//    fun onHelloRequest(){
//        type.write()
//    }
//}
//
//
//class ClientHello : Layout() {
//    private val clientVersion = integer(2)
//    private val clientRandom = integer(32)
//    private val sessionId = stream(32)
//    private val cipherSuites = stream(0xFFFF - 2, CipherLayout().repeat()) // TODO map to sequence of ciphers
//    private val compressionMethods = stream(0xFF - 1) // TODO map to sequence of methods
//    private val extensions = Layout.either(
//        Layout.Nothing,
//        stream(ExtensionLayout().repeat())
//    ) // TODO map to extensions. TODO nothing if parent done
//
//    fun read(listener: TlsHandshake) {
//        listener.onClientHello()
//    }
//
//    fun write() {
//
//    }
//}
//
//abstract class ExtensionLayout : Layout() {
//    fun repeat(): Layout = TODO()
//}
//
//class CipherLayout : Layout() {
//    fun repeat(): Layout = TODO()
//}
//
//
//interface Injection {
//    interface Of<A, B> {
//        fun apply(value: A): B
//        fun coApply(value: B): A
//    }
//
//    interface OfInt<A> {
//        fun apply(value: Int): A
//        fun coApply(value: A): Int
//    }
//}
//
//interface TlsHandshake {
//    fun onHelloRequest()
//    fun onClientHello()
//}
//
//open class Layout {
//    internal val fields = mutableListOf<Struct>()
//
//    fun size(): Int = TODO()
//
//    fun integer(size: Int): Struct.Of<BigInteger> = ArrayOfBytes(this, size).map(::BigInteger, BigInteger::toByteArray)
//    fun stream(limit: Int) = StreamOfBytes(this, limit)
//    fun <A : Layout> stream(layout: A): A = layout.also { LaidOutStream(this, it) }
//    fun <A : Layout> stream(limit: Int, layout: A): A = layout.also { LaidOutStream(this, it) }
//}
//
//class StreamOfBytes(layout: Layout, limit: Int) : Struct
//class LaidOutStream(parent: Layout, layout: Layout) : Struct
//
//interface BinarySource {
//    fun readInt(at: Int, bytes: Int): Int
//}
//
//interface BinaryTarget {
//    fun writeInt(at: Int, bytes: Int, value: Int)
//}
//
//interface Struct {
//    interface OfInt : Struct {
//        fun read(source: BinarySource): Int
//        fun write(target: BinaryTarget, value: Int)
//        fun <A> map(f: Injection.OfInt<A>): Of<A> = MappedInt(this, f)
//    }
//
//    interface Of<A> : Struct {
//        fun read(source: BinarySource): A
//        fun write(target: BinaryTarget, value: A)
//        fun <B> map(f: (A) -> B, g: (B) -> A): Of<B> = TODO()
//        fun <B> map(f: Injection.Of<A, B>): Of<B> = TODO()
//    }
//
//    interface SeqOf<A> {
//        fun read(source: BinarySource): Array<A>
//    }
//}
//
//class ArrayOfBytes(layout: Layout, bytes: Int) : Struct.Of<ByteArray> {
//
//}
//
//class IntStruct(layout: Layout, private val size: Int) : Struct.OfInt {
//    override fun read(source: BinarySource): Int = source.readInt(at = index, bytes = size)
//    override fun write(target: BinaryTarget, value: Int) = target.writeInt(at = index, bytes = size, value)
//}
//
//class MappedInt<A>(private val struct: Struct.OfInt, private val f: Injection.OfInt<A>) : Struct.Of<A> {
//    override fun read(source: BinarySource): A = struct.read(source).let(f::apply)
//    override fun write(target: BinaryTarget, value: A) = f.coApply(value).let { struct.write(target, it) }
//}
//
//val layout = Layout()
//
//class Cipher
//class Ciphers(layout: Layout) : Struct.SeqOf<Cipher> {
//    override fun read(source: BinarySource): Array<Cipher> {
//        var offset = 0
//        TODO("Not yet implemented")
//    }
//
//}