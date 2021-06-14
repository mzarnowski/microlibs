//package dev.mzarnowski.io.binary.v3
//
//class ClientHello(val version: Int, val random: ByteArray, val session: ByteArray, val ciphers: Array<Cipher>)
//
//object ClientHelloEnc : Layout.Of<ClientHello>() {
//    val version = int(1)
//    val random = bytes(32)
//    val session = bytes(0, 32)
//    val ciphers = seqOf(CipherEncoding, size = Encoding.OfInt(2), sizeInBytes = true)
//    val compressions = seqOf(CompressionEncoding, size = Encoding.OfInt(2), sizeInBytes = true)
//    val extensions = seqOf(ExtensionEncoding, size = Encoding.OfInt(2), sizeInBytes = true)
//
//    fun write()
//}
//
//enum class Cipher
//enum class Compression
//interface Extension
//
//object CipherEncoding : Encoding.OfRefOnInt<Cipher>(2, ::cipherToInt)
//object CompressionEncoding : Encoding.OfRefOnInt<Compression>(bytes = 1, ::compressionToInt)
//object ExtensionEncoding : Encoding.Of<Extension> {
//    override fun write(output: ByteArray, offset: Int, value: Extension): Int {
//        TODO("Not yet implemented")
//    }
//
//}
//
//private fun cipherToInt(cipher: Cipher): Int = TODO()
//private fun compressionToInt(compression: Compression): Int = TODO()