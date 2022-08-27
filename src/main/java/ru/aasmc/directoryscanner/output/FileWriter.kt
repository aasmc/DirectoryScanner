package ru.aasmc.directoryscanner.output

import java.io.Closeable
import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE


class FileWriter(
    file: Path,
    charset: Charset
) : Closeable {
    private val writer: PrintWriter = PrintWriter(
        Files.newBufferedWriter(file, charset, CREATE, APPEND),
        true
    )

    fun writeln(line: String) {
        writer.println(line)
    }

    fun write(line: String) {
        writer.print(line)
    }

    fun writeLines(lines: Collection<String>) {
        lines.forEach(::write)
    }

    override fun close() {
        writer.close()
    }

    fun writelnLines(lines: Collection<String>) {
        lines.forEach(::writeln)
    }
}