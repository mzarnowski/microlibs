package dev.mzarnowski.lang.parser

sealed class Composed<A>(val rules: List<Rule<A>>) : Rule<A> {
    abstract infix fun and(that: Rule<A>): Composed<A>
    abstract infix fun or(that: Rule<A>): Composed<A>

    class And<A>(rules: List<Rule<A>>) : Composed<A>(rules) {
        override fun or(that: Rule<A>): Composed<A> = Or(listOf(this, that))
        override fun and(that: Rule<A>): Composed<A> = when (that) {
            is And -> And(this.rules + that.rules)
            else -> And(rules + that)
        }

        override fun match(input: A, from: Int): Int {
            var offset = from
            for (rule in rules) {
                val matched = rule.match(input, offset)
                if (matched < 0) return matched
                offset += matched
            }

            return offset - from
        }
    }

    class Or<A>(rules: List<Rule<A>>) : Composed<A>(rules) {
        override fun and(that: Rule<A>): Composed<A> = And(listOf(this, that))
        override fun or(that: Rule<A>): Composed<A> = when (that) {
            is Or -> Or(this.rules + that.rules)
            else -> Or(listOf(this, that))
        }

        override fun match(input: A, from: Int): Int {
            for (rule in rules) {
                val matched = rule.match(input, from)
                if (matched >= 0) return matched
            }

            return -1
        }
    }
}