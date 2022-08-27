package ru.aasmc.directoryscanner.input

import java.util.regex.Pattern

/**
 * Regex to validate path to directory in windows
 */
internal val winPattern1: Pattern =
    """
            ^([a-zA-Z]:\\|[a-zA-Z]:|\\)(\\[\w.\-_\s]+|\\\\[\w.\-_\s]+)*\\$
        """.trimIndent().toRegex().toPattern()

/**
 * Regex to validate path to directory in windows.
 * Uses forward slashes as delimiters
 */
internal val winPattern2: Pattern =
    """
            ^([a-zA-Z]:)(/[\w-_.\s]+)+/$
        """.trimIndent()
        .toRegex()
        .toPattern()

/**
 * Regex to validate path to directory on UNIX systems.
 */
internal val unixPattern = "^/([\\w-_.\\s\\\\]+/)*$"
    .toRegex()
    .toPattern()

internal val patterns = listOf(
    winPattern1,
    winPattern2,
    unixPattern
)