package ru.aasmc.directoryscanner.input

interface InputParser {
    fun registerExcluders(vararg excluders: Excluder)

    fun parse(vararg params: String): ParseResult
}