package dev.mzarnowski.lang.parser

interface Foo {
    class Map<A, B>(f: (A) -> B) : Foo
}

data class Token(val from: Int, val length: Int) : Foo
