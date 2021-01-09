package dev.mzarnowski.lang.parser

fun interface Match : Rule<String> {
    infix fun and(that: Match): Matcher = Matcher(Composed.And(listOf(this, that)))
    infix fun <A> and(that: Parse<A>): Parser<A> = Parser(Composed.And(listOf(this.adapt(Input::text), that)))

    companion object {
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
