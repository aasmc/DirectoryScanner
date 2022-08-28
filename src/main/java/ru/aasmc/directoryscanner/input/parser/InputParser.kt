package ru.aasmc.directoryscanner.input.parser

import ru.aasmc.directoryscanner.input.excluder.PathExcluder

interface InputParser {
    fun registerExcluders(vararg excluders: PathExcluder)

    fun parse(vararg params: String): ParseResult
}