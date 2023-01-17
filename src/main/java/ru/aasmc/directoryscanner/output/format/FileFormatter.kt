package ru.aasmc.directoryscanner.output.format

import ru.aasmc.directoryscanner.output.FileReader
import ru.aasmc.directoryscanner.output.FileWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

abstract class FileFormatter(
    private val file: Path,
    private val charset: Charset
) {
    /**
     * Formats an entry with info about a file.
     */
    abstract fun formatEntry(path: Path, attrs: BasicFileAttributes): String


    /**
     * Common logic of file formatting.
     * After the method completes our private [file] field is sorted by
     * alphabet and formatted.
     *
     * @param sortedFile - alphabetically sorted file that contains absolute paths to files
     *                     which were found during scanning.
     */
    fun format(sortedFile: Path) {
        FileReader(sortedFile, charset).use { reader ->
            FileWriter(file, charset).use { writer ->
                // main cycle that creates a sorted file
                var filePath = reader.readLine()
                while (filePath != null) {
                    // Read lines from a sorted file while there are any.
                    // Every line is an absolute path to a found file
                    val path = Paths.get(filePath)
                    if (Files.exists(path)) { // If current file exists in the system at the time of writing
                        val attrs = Files.readAttributes(path, BasicFileAttributes::class.java)
                        // form a formatted line with info about the file
                        val formattedEntry = formatEntry(path, attrs)
                        // write it to the output file
                        writer.write(formattedEntry)
                    }
                    filePath = reader.readLine()
                }

                sortedFile.toFile().deleteOnExit()
            }
        }
    }
}