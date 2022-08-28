package ru.aasmc.directoryscanner.scan.filter

import ru.aasmc.directoryscanner.input.validator.PathValidator
import java.nio.file.Path

sealed class Filter(open val validator: PathValidator? = null) {

    abstract fun filter(path: Path): Boolean

    object EmptyFilter : Filter() {
        override fun filter(path: Path): Boolean = false
    }

    data class DirectoryFilter(
        val dirsToFilter: List<Path>,
        override val validator: PathValidator
    ) : Filter(validator) {
        override fun filter(path: Path): Boolean {
            return if (validator.isDirectory(path)) {
                filterDir(path)
            } else {
                false
            }
        }

        private fun filterDir(path: Path): Boolean {
            return dirsToFilter
                .contains(path) ||
                    dirsToFilter.any { parent ->
                        isParentFor(possibleChild = path, parent = parent)
                    }
        }

        private fun isParentFor(possibleChild: Path, parent: Path): Boolean {
            return possibleChild.startsWith(parent)
        }
    }

    data class FileFilter(
        val filesToFilter: List<Path>,
        override val validator: PathValidator
    ) : Filter(validator) {
        override fun filter(path: Path): Boolean {
            return if (validator.isFile(path)) {
                filterFile(path)
            } else {
                false
            }
        }

        private fun filterFile(path: Path): Boolean {
            return filesToFilter.contains(path)
        }
    }
}
