package ru.aasmc.directoryscanner.output

import java.io.BufferedReader
import java.io.Closeable
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class FileReader(
    fileToRead: Path,
    charset: Charset
) : Closeable {
    private val reader: BufferedReader = Files.newBufferedReader(fileToRead, charset)

    fun readLine(): String? {
        return reader.readLine()
    }

    override fun close() {
        reader.close()
    }
}