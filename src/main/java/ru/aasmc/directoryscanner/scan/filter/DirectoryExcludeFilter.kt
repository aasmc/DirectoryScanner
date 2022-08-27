package ru.aasmc.directoryscanner.scan.filter

import ru.aasmc.directoryscanner.input.DirectoryValidator
import java.nio.file.Path

/**
 * Class for filtering directories. Has one direct descendant
 * {@link DefaultDirectoryExcludeFilter}
 */
abstract class DirectoryExcludeFilter(
    protected val dirsToFilter: List<Path>,
    private val validator: DirectoryValidator
) : ExcludeFilter() {

    override fun isEmpty(): Boolean {
        return dirsToFilter.isEmpty()
    }

    protected abstract fun filterDirectory(path: Path): Boolean

    override fun filter(path: Path): Boolean {
        return if (validator.isDirectory(path)) {
            filterDirectory(path)
        } else {
            false
        }
    }
}