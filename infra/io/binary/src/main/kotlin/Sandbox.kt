//package dev.mzarnowski.io.binary
//
//import java.io.InputStream
//
//class Record(val type: Int, val version: Int, val fragment: ByteArray)
//
//class RecordLayout(private val parent: ByteLayout) : Layout.Of<Record> {
//    private val type = parent.int()
//    private val version = parent.int()
//    private val fragment = parent.stream(0xFFFF - 1)
//
//    override fun read(): Record {
//        val type = type.read()
//        val version = version.read()
//        val fragment: ByteArray = fragment.read()
//        return Record(type, version, fragment)
//    }
//
//    override fun write(value: Record) {
//        type.write(value.type)
//        version.write(value.version)
//        fragment.write(value.fragment)
//    }
//}
//
//class TlsRecordHandler(inputStream: InputStream, val f: (Record) -> Unit) {
//    val layout = ByteLayout()
//    val recordLayout = RecordLayout(layout)
//
//    fun start() {
//        recordLayout.read().run(f)
//    }
//}