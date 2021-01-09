package dev.mzarnowski.lang.parser

abstract class Component<Input, Identity>(val rules: Composed<Input>) {
    abstract infix fun and(that: Match): Component<Input, *>
    abstract infix fun or(that: Identity): Component<Input, *>
}
