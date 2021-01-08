package dev.mzarnowski.lang.parser

internal const val FAILED = -1

abstract class Rule internal constructor() {
    abstract fun match(input: Input, offset: Int): Int

    open infix fun and(that: Rule): Rule = Sequence(this, that)
    open infix fun or(that: Rule): Rule = Alternative(this, that)

    internal class Sequence(vararg val rules: Rule) : Rule() {
        override fun and(that: Rule): Rule = Sequence(*rules, that)

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

    internal class Alternative(vararg val rules: Rule) : Rule() {
        override fun or(that: Rule): Rule = Alternative(*rules, that)

        override fun match(input: Input, offset: Int): Int {
            for (rule in rules) {
                val matched = rule.match(input, offset)
                if (matched != FAILED) return matched
            }
            return FAILED
        }
    }
}

fun main() {
    val input = Input("ala ma kota")
    val rule = MatchString("ala") and Capture(MatchString(" ma ")) and MatchString("kota")
    val result = rule.match(input, 0)
    println(result)
    println(input.tokens)
}