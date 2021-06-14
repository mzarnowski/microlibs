package dev.mzarnowski.io.binary.v5

import javax.crypto.Cipher

class Join(structure: Structure) : Layout(structure) {
    val key = SecretKey(structure, TODO())
    val payload = raw(1024).let
}

// TODO 512 should be derived from cipher
class SecretKey(parent: Structure, val cipher: Cipher) : Layout(parent) {
    private val key = Encrypted(raw(512 /*, TODO Padding.None*/), cipher)
    private val iv = Field.Raw(parent, TODO("Raw encoding of n bytes"))

    // TODO read/write methods
    fun read(): SecretKey = TODO()

    fun write(secretKey: SecretKey) {

    }
}

// TODO must be buffered, since fields might come in any order
class Authenticated<A : Structure>(parent: Structure, fragment: (Structure) -> A) : BufferedFragment(TODO(), TODO()) {
    // TODO mustn't be a layout, because we need control on how to read the content
    private val fragment: Structure = fragment(parent.fragment(512)) // TODO should be a stream, not fragment(512)

    // must come after materializing fragment
    // also, TODO should use SHAEncoding or similar
    private val mac = Field.Raw(parent, RawEncoding)

    override fun flush() = mac.write(TODO(buffer), 0)
}

class Encrypted(bytes: Field.Raw, private val cipher: Cipher) : BufferedFragment(bytes, 512) {
    override fun flush() {
        val output = cipher.doFinal(buffer.bytes.array())
        bytes.write(output)
    }
}