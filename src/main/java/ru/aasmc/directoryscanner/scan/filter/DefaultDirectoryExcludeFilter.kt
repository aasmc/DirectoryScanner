package ru.aasmc.directoryscanner.scan.filter

import ru.aasmc.directoryscanner.input.DirectoryValidator
import java.nio.file.Path

class DefaultDirectoryExcludeFilter(
    dirsToFilter: List<Path>,
    validator: DirectoryValidator
) : DirectoryExcludeFilter(dirsToFilter, validator) {

    override fun filterDirectory(path: Path): Boolean {
        // We filter the directory if
        return dirsToFilter
            .contains(path) || // it explicitly contains the directory we must filter
                dirsToFilter.any { parent ->
                    // or this directory is a child of the directory we must filter
                    isParentFor(possibleChild = path, parent = parent)
                }
    }

    private fun isParentFor(possibleChild: Path, parent: Path): Boolean {
        return possibleChild.startsWith(parent)
    }
}