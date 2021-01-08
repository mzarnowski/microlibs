package dev.mzarnowski.lang.parser

class MatchString(val pattern: String) : Match() {
    override fun match(input: Input, offset: Int): Int {
        val text = input.source
        var matched = 0
        while (matched < pattern.length) {
            val at = offset + matched
            if (at == text.length) return -1
            if (text[at] != pattern[matched]) return -1
            matched += 1
        }
        return matched
    }
}