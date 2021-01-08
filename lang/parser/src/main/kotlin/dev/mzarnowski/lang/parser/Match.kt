package dev.mzarnowski.lang.parser

internal class Map<A, B>(private val f: (A) -> B) : Rule<B>() {
    override fun match(input: Input, offset: Int): Int {
        input.push(f)
        return 0
    }
}

abstract class Match : Rule<String>() {
    final override fun <B> and(that: Rule<B>): Rule<B> = super.and(that)
    final override fun or(that: Rule<String>): Rule<String> = super.or(that)

    fun <A> map(f: (String) -> A): Rule<A> = Capture(this) and Map(f)

    companion object {
        operator fun invoke(f: (Char) -> Boolean): Match = object : Match() {
            override fun match(input: Input, offset: Int): Int {
                val text = input.source
                var matched = 0

                while (f(text[offset + matched])) matched++
                return matched
            }
        }

        operator fun invoke(pattern: String): Match = object : Match() {
            override fun match(input: Input, offset: Int): Int {
                val text = input.source
                var matched = 0

                while (matched < pattern.length) {
                    if (text[offset + matched] == pattern[matched]) matched++
                    else return FAILED
                }

                return matched
            }
        }
    }
}

