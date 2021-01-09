package dev.mzarnowski.lang.parser

import java.util.function.Consumer

data class Input(val text: String) {
    val stack = mutableListOf<Any>()
    val ops = mutableListOf<Op>()

    fun <A> pop(): A {
        @Suppress("UNCHECKED_CAST")
        return stack.removeLast() as A
    }

    fun <A : Any> push(value: A) {
        stack.add(value)
    }
}

fun interface Op : Consumer<Input> {
    companion object {
        fun <A : Any, B : Any> map(f: (A) -> B): Op = Op { stack ->
            val top = stack.pop<A>()
            stack.push(f(top))
        }
    }
}