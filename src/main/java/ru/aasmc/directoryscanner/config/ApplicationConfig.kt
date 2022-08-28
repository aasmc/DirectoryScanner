package ru.aasmc.directoryscanner.config

import ru.aasmc.directoryscanner.input.parser.InputParser
import ru.aasmc.directoryscanner.input.excluder.PathExcluder
import ru.aasmc.directoryscanner.output.format.FileFormatter
import java.nio.charset.Charset
import java.nio.file.Path

/**
 * Interface responsible for configuring the app.
 * This is the main point for extending the app.
 */
interface ApplicationConfig {

    fun outputFilePath(): Path

    fun outputFileCharset(): Charset

    fun fileFormatter(): FileFormatter

    fun inputParamsExcluders(): List<PathExcluder>

    fun inputParamsParser(): InputParser

    fun outputEntryBufferSize(): Int {
        return 2 * MByte
    }

    companion object {
        const val Byte = 1
        const val KByte = 1024
        const val MByte = 1024 * KByte
        const val GByte = 1024 * MByte
    }
}