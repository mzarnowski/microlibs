package dev.mzarnowski.lang.parser

fun main() {
    val rule = Match("ala ma ") and Capture(Char::isDigit) and Match(" koty")
    val result = rule.parse("ala ma 32 koty")
    println(result)
}