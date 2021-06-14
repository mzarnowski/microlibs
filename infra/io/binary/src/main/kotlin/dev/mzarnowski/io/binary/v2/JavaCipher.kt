package dev.mzarnowski.io.binary.v2

//class JavaCipher(parent: Binding) : DynamicBlock(parent) {
//    fun includeInAAD(field: Structure) {
//        require(parentOf(field) == this)
//    }
//}

private fun parentOf(structure: Structure): Layout = when (structure) {
    is DynamicBlock -> structure.layout
    is StaticBlock -> structure.layout
    is IntField -> structure.layout
    is ByteArrayField -> structure.layout
    else -> throw UnsupportedOperationException(structure.javaClass.name)
}