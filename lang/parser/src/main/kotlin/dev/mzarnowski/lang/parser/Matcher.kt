package dev.mzarnowski.lang.parser

class Matcher(val step: Step<String>) : Match() {
//    infix fun <A> and(that: Parse<A>): Parser<A> = Parser(Composed.And(listOf(rules.adapt(Input::text), that)))

    override fun match(input: String, from: Int): Int = step.match(input, from)
}