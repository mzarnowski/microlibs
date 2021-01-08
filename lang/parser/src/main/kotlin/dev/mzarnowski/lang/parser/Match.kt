package dev.mzarnowski.lang.parser

abstract class Match : Rule() {
    final override fun and(that: Rule): Rule = super.and(that)
    final override fun or(that: Rule): Rule = super.or(that)
}