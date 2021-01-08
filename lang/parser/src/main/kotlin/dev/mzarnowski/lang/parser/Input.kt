package dev.mzarnowski.lang.parser

class Input(val source: String) {
    internal val tokens = mutableListOf<Token>()

    operator fun get(offset: Int): Char = source[offset]

    fun push(offset: Int, it: Int) {
        tokens.add(Token(offset, it))
    }
}