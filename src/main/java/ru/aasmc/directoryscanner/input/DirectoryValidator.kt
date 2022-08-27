package ru.aasmc.directoryscanner.input

import java.nio.file.Path

interface DirectoryValidator {
    fun doesExist(path: Path): Boolean
    fun notExists(path: Path): Boolean
    fun isDirectory(path: Path): Boolean
    fun isNotDirectory(path: Path): Boolean
}