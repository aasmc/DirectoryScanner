package ru.aasmc.directoryscanner.input.validator

import java.nio.file.Path
import java.util.regex.Matcher

interface PathValidator {
    fun doesExist(path: Path): Boolean
    fun notExists(path: Path): Boolean
    fun isDirectory(path: Path): Boolean
    fun isNotDirectory(path: Path): Boolean
    fun isFile(path: Path): Boolean
    fun validateDirCorrect(
        path: String,
        params: Array<out String>,
        matchers: List<Matcher>,
        block: (p: Path) -> Unit
    )
}