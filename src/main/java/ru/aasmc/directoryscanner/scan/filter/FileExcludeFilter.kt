package ru.aasmc.directoryscanner.scan.filter

import java.nio.file.Path

abstract class FileExcludeFilter : ExcludeFilter() {

    abstract override fun filter(path: Path): Boolean

}