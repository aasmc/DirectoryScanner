package ru.aasmc.directoryscanner.input.parser

import ru.aasmc.directoryscanner.scan.filter.Filter
import java.nio.file.Path

data class ParseResult(
    val dirsToScan: List<Path>,
    val filters: List<Filter>
)
