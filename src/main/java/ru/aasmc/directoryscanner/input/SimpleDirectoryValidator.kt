package ru.aasmc.directoryscanner.input

import ru.aasmc.directoryscanner.input.DirectoryValidator
import java.nio.file.Files
import java.nio.file.Path

class SimpleDirectoryValidator : DirectoryValidator {

    override fun doesExist(path: Path): Boolean = Files.exists(path)

    override fun notExists(path: Path): Boolean {
        return !doesExist(path)
    }

    override fun isDirectory(path: Path): Boolean = Files.isDirectory(path)

    override fun isNotDirectory(path: Path): Boolean {
        return !isDirectory(path)
    }

}