package dev.mzarnowski.lang.parser

class Input(val source: String) {
    internal val tokens = mutableListOf<Foo>()

    operator fun get(offset: Int): Char = source[offset]

    internal fun push(offset: Int, it: Int) {
        tokens.add(Token(offset, it))
    }

    internal fun <A, B> push(f: (A) -> B) {
        tokens.add(Foo.Map(f))
    }
}