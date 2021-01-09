package dev.mzarnowski.lang.parser

data class Input(val text: String) {
    val stack = mutableListOf<Any>()
}
