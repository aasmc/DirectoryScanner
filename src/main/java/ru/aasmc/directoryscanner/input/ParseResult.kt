package ru.aasmc.directoryscanner.input

import ru.aasmc.directoryscanner.scan.filter.ExcludeFilter
import java.nio.file.Path

data class ParseResult(
    val dirsToScan: List<Path>,
    val filters: List<ExcludeFilter>
)
