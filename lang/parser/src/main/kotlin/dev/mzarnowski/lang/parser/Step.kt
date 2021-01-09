package dev.mzarnowski.lang.parser

internal const val FAILED = -1

abstract class Step<Input> {
    abstract fun match(input: Input, from: Int): Int
    fun <B> adapt(f: (B) -> Input): Step<B> = Adapter(this, f)

    // todo make internal?
    internal open infix fun and(that: Step<Input>): Step<Input> {
        return mutableListOf<Step<Input>>().let { steps ->
            if (this is And) steps += this.steps else steps += this
            if (that is And) steps += that.steps else steps += that
            And(steps)
        }
    }

    internal open infix fun or(that: Step<Input>): Step<Input> {
        return mutableListOf<Step<Input>>().let { steps ->
            if (this is Or) steps += this.steps else steps += this
            if (that is Or) steps += that.steps else steps += that
            Or(steps)
        }
    }
}

internal class And<A>(val steps: List<Step<A>>) : Step<A>() {
    override fun match(input: A, from: Int): Int {
        var offset = from
        for (rule in steps) {
            val matched = rule.match(input, offset)
            if (matched < 0) return matched
            offset += matched
        }

        return offset - from
    }
}

internal class Or<A>(val steps: List<Step<A>>) : Step<A>() {
    override fun match(input: A, from: Int): Int {
        for (rule in steps) {
            val matched = rule.match(input, from)
            if (matched >= 0) return matched
        }

        return FAILED
    }
}

internal class Adapter<From, To>(val next: Step<To>, val f: (From) -> To) : Step<From>() {
    override fun match(input: From, from: Int): Int {
        val adapted = f(input)
        return next.match(adapted, from)
    }
}

abstract class Parse<A> : Step<Input>() {
    infix fun <B> then(that: Parse<B>): Parse<B> = when (this) {
        is Parser -> Parser(this.step and that)
        else -> Parser(super.and(that))
    }

    infix fun then(matcher: Match): Parse<A> = this and matcher.adapt(Input::text)

    override fun and(that: Step<Input>): Parse<A> = when (this) {
        is Parser -> Parser(this.step and that)
        else -> Parser(super.and(that))
    }

    override fun or(that: Step<Input>): Parse<A> = when (this) {
        is Parser -> Parser(this.step or that)
        else -> Parser(super.or(that))
    }

    fun parse(input: String): A? = parse(Input(input), 0)

    private fun parse(input: Input, from: Int): A? {
        val matched = match(input, from)
        if (matched < 0) return null

        @Suppress("UNCHECKED_CAST")
        return input.stack.last() as A
    }

    fun <B : Any> map(f: (A) -> B): Parse<B> = Parse { input, from ->
        this.match(input, from).also {
            if (it >= 0) input.ops.add(Op.map(f))
        }
    }

    companion object {
        operator fun invoke(p: (Char) -> Boolean): Parse<String> = Capture(Match(p))
        operator fun <A> invoke(f: (Input, Int) -> Int) = object : Parse<A>() {
            override fun match(input: Input, from: Int): Int = f(input, from)
        }
    }
}

class Capture(private val matcher: Match) : Parse<String>() {
    override fun match(input: Input, from: Int): Int {
        return matcher.match(input.text, from).also {
            if (it >= 0) input.stack += input.text.subSequence(from, from + it)
        }
    }

    companion object {
        operator fun invoke(p: (Char) -> Boolean): Capture = Capture(Match(p))
    }
}