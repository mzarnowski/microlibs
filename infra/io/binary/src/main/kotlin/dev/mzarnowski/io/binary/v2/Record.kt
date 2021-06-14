package dev.mzarnowski.io.binary.v2


//class Record(val type: Int, val version: Int, val fragment: ByteArray)
//
//class RecordLayout {
//    private val layout = RootLayout(ByteArray(4096))
//    private val type = layout.int(1)
//    private val version = layout.int(2)
//    private val fragment = layout.stream(3)
//
//    fun write(record: Record, output: ByteArray, offset: Int) {
//        layout.writeTo(output, offset)
//        type.write(record.type)
//        version.write(record.version)
//        fragment.append(record.fragment)
//        layout.flush()
//    }
//}
//
//class Handshake(val type: Int, payload: ByteArray)
//class HandshakeLayout(layout: Layout, index: Int) : StreamField(layout, index, layout.int(3)){
//    val type = int
//}