package dev.mzarnowski.lang.parser

class Capture(private val rule: Rule) : Match() {
    override fun match(input: Input, offset: Int): Int {
        return rule.match(input, offset).also {
            if (it > 0) input.push(offset, it)
        }
    }
}