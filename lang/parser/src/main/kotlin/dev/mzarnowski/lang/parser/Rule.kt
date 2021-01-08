package dev.mzarnowski.lang.parser

internal const val FAILED = -1

abstract class Rule<A> internal constructor() {
    abstract fun match(input: Input, offset: Int): Int

    open infix fun <B> and(that: Rule<B>): Rule<B> = Sequence(this, that)
    open infix fun or(that: Rule<A>): Rule<A> = Alternative(this, that)

    internal class Sequence<A>(vararg val rules: Rule<*>) : Rule<A>() {
        override fun <B> and(that: Rule<B>): Rule<B> = when (that) {
            is Sequence -> Sequence(*this.rules, *that.rules)
            else -> Sequence(*this.rules, that)
        }

        override fun match(input: Input, offset: Int): Int {
            var parsed = 0
            for (rule in rules) {
                val matched = rule.match(input, offset + parsed)
                if (matched == FAILED) return FAILED
                parsed += matched
            }
            return parsed
        }
    }

    internal class Alternative<A>(vararg val rules: Rule<A>) : Rule<A>() {
        override fun or(that: Rule<A>): Rule<A> = when (that) {
            is Alternative -> Alternative(*this.rules, *that.rules)
            else -> Alternative(*this.rules, that)
        }

        override fun match(input: Input, offset: Int): Int {
            for (rule in rules) {
                val matched = rule.match(input, offset)
                if (matched != FAILED) return matched
            }
            return FAILED
        }
    }
}