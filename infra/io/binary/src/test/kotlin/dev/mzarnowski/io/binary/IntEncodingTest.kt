//package dev.mzarnowski.io.binary
//
//import dev.mzarnowski.io.binary.encoding.IntEncoding
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.ValueSource
//
//class IntEncodingTest {
//
//    @ParameterizedTest
//    @ValueSource(ints = [1, 2, 3, 4])
//    fun `encodes int`(bytes: Int) {
//        val mask = (0 until bytes).sumOf { 0xFF shl (8 * it) }
//        val value = 0x12345678
//
//        val encoding = IntEncoding(bytes)
//
//        val buffer = ByteArray(bytes)
//        encoding.write(value, buffer, 0) // note that the surplus bytes are ignored
//        val decoded = encoding.read(buffer, 0)
//
//        assertThat(Integer.toHexString(decoded)).isEqualTo(Integer.toHexString(value and mask))
//    }
//}