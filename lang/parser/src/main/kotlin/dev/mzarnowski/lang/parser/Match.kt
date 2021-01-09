package dev.mzarnowski.lang.parser

abstract class Match : Step<String>() {
    fun matches(text: String, from: Int): Boolean = match(text, from) >= 0

    override fun and(that: Step<String>): Step<String> = when (this) {
        is Matcher -> Matcher(this.step and that)
        else -> Matcher(super.or(that))
    }

    override fun or(that: Step<String>): Step<String> = when (this) {
        is Matcher -> Matcher(this.step or that)
        else -> Matcher(super.or(that))
    }

    companion object {
        operator fun invoke(f: (String, Int) -> Int) = object : Match() {
            override fun match(input: String, from: Int): Int = f(input, from)
        }

        operator fun invoke(f: (Char) -> Boolean): Match = Match { input, from ->
            if (f(input[from])) {
                var offset = from + 1
                while (offset < input.length && f(input[offset])) offset++
                offset - from
            } else FAILED
        }

        operator fun invoke(pattern: CharSequence): Match = Match { input, from ->
            var matched = 0
            while (matched < pattern.length) {
                if (input[from + matched] == pattern[matched]) matched++
                else return@Match FAILED
            }

            matched
        }

        operator fun invoke(char: Char): Match = Match { input, from ->
            if (input[from] == char) 1 else FAILED
        }

        val Whitespace = Match(Char::isWhitespace)
    }

}
