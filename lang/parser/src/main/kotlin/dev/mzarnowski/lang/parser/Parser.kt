package dev.mzarnowski.lang.parser

class Parser<A>(val step: Step<Input>) : Parse<A>(){
    override fun match(input: Input, from: Int): Int = step.match(input, from)
}