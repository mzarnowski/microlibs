package dev.mzarnowski.lang.parser

class Matcher(rules: Composed<String>) : Component<String, Match>(rules) {
    override fun and(that: Match): Matcher = Matcher(rules and that)
    override fun or(that: Match): Matcher = Matcher(rules or that)
    infix fun <A> and(that: Parse<A>): Parser<A> = Parser(Composed.And(listOf(rules.adapt(Input::text), that)))

    fun matches(text: String, from: Int): Boolean {
        val matched = rules.match(text, from)
        return matched >= 0
    }
}