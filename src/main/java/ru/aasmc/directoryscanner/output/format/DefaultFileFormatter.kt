package ru.aasmc.directoryscanner.output.format

import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat

class DefaultFileFormatter(
    file: Path,
    charset: Charset
) : FileFormatter(file, charset) {

    /**
     * Creates a formatted string with info about the given file.
     */
    override fun formatEntry(path: Path, attrs: BasicFileAttributes): String {
        val name = path.toAbsolutePath().toString()
        val date = attrs.creationTime()
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val dateCreated = sdf.format(date.toMillis())
        val size = attrs.size()

        return "[\n" +
                " name=" +
                name +
                "\n" +
                " date=" +
                dateCreated +
                "\n" +
                " size=" +
                size +
                "]"
    }
}