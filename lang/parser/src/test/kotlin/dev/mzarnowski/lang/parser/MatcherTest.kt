package dev.mzarnowski.lang.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MatcherTest {
    @Test
    fun matches_character() {
        val matcher = Match('c')
        assertThat(matcher.match("c", 0)).isEqualTo(1)
        assertThat(matcher.match("cd", 0)).isEqualTo(1)
        assertThat(matcher.match("d", 0)).isEqualTo(FAILED)
    }

    @Test
    fun matches_string() {
        val matcher = Match("foo")
        assertThat(matcher.match("foo", 0)).isEqualTo(3)
        assertThat(matcher.match("foo ", 0)).isEqualTo(3)
        assertThat(matcher.match(" foo", 0)).isEqualTo(FAILED)
    }

    @Test
    fun matches_predicate() {
        val matcher = Match(Char::isDigit)
        assertThat(matcher.match("12345", 0)).isEqualTo(5)
        assertThat(matcher.match("123A", 0)).isEqualTo(3)
        assertThat(matcher.match("A123", 0)).isEqualTo(FAILED)
    }

    @Test
    fun matches_whitespace() {
        val matcher = Match.Whitespace
        assertThat(matcher.match(" \t\n\r\n", 0)).isEqualTo(5)
        assertThat(matcher.match(" a", 0)).isEqualTo(1)
        assertThat(matcher.match("a ", 0)).isEqualTo(FAILED)
    }
}