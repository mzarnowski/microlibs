package dev.mzarnowski.lang.parser

internal const val FAILED = -1

fun interface Rule<in Input> {
    fun match(input: Input, from: Int): Int
    fun <B> adapt(f: (B) -> Input): Rule<B> = Adapter(this, f)
}

class Adapter<From, To>(val next: Rule<To>, val f: (From) -> To) : Rule<From> {
    override fun match(input: From, from: Int): Int {
        val adapted = f(input)
        return next.match(adapted, from)
    }
}

interface Parse<A> : Rule<Input>

class Capture(private val matcher: Match) : Parse<CharSequence> {
    override fun match(input: Input, from: Int): Int {
        return matcher.match(input.text, from).also {
            if (it >= 0) input.stack += input.text.subSequence(from, from + it)
        }
    }

    companion object {
        operator fun invoke(p: (Char) -> Boolean): Capture = Capture(Match(p))
    }
}