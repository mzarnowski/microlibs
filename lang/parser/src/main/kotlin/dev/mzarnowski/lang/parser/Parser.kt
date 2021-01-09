package dev.mzarnowski.lang.parser

class Parser<A>(rules: Composed<Input>) : Component<Input, Parse<A>>(rules) {
    fun parse(input: String): A? = parse(Input(input), 0)
    private fun parse(input: Input, from: Int): A? {
        val matched = rules.match(input, from)
        if (matched < 0) return null

        @Suppress("UNCHECKED_CAST")
        return input.stack.last() as A
    }

    override fun and(that: Match): Parser<A> = Parser(rules and that.adapt(Input::text))
    override fun or(that: Parse<A>): Parser<A> = Parser(rules and that)
}