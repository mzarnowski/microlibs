package dev.mzarnowski.io.binary.v2


// block.repeat(n)
// stream shouldn't be used as a base for sequence - it's better to prepend element count than byte count
//   alternatively, stream should be a sequence of bytes
class Sequence(separator: ByteArray) {

}

class SequenceWithSeparator<A, B : Structure>()