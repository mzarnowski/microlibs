package dev.mzarnowski.lang.parser

fun main() {
    val input = Input("ala ma 32 koty")
    val rule = Match("ala ma ") and Match(Char::isDigit).map(String::toInt) and Match(" koty")
    val result = rule.match(input, 0)
    println(result)
    println(input.tokens)
}