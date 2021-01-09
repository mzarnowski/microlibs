package dev.mzarnowski.lang.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParseTest {
    @Test
    fun capture_matched() {
        val matcher = Capture(Char::isDigit)
        Input("123 foo").let {
            assertThat(matcher.match(it, 0)).isEqualTo(3)
            assertThat(it.stack.size).isEqualTo(1)
            assertThat(it.stack.first()).isEqualTo("123")
        }
    }

    @Test
    fun map_captured() {
        val matcher = Capture(Char::isDigit).map { it.toInt() + 1 }
        val input = Input("123 foo")

        // when matched
        val matched = matcher.match(input, 0)

        assertThat(matched).isEqualTo(3)
        assertThat(input.stack.size).isEqualTo(1)
        assertThat(input.stack.first()).isEqualTo("123")
        assertThat(input.ops.size).isEqualTo(1)

        // when reduced
        input.ops.first().accept(input)

        // then
        assertThat(input.stack.first()).isEqualTo(124)

        // then
    }
}