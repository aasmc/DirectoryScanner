package ru.aasmc.directoryscanner.scan.filter

import java.nio.file.Path

/**
 * Class responsible for file filtering.
 * It has two abstract descendants:
 *   1. {@link DirExcludeFilter} - directory filter
 *   2. {@link FileExcludeFilter} - file filter
 *
 * Instances of the type are created by {@link ru.aasmc.directoryscanner.input.Excluder}
 * after parsing input parameters and searching for files to be excluded from scanning.
 */
abstract class ExcludeFilter {
    /**
     * Main method for filtering.
     */
    abstract fun filter(path: Path): Boolean

    /**
     * Checks if the filter is empty (has nothing to filter).
     */
    abstract fun isEmpty(): Boolean

    companion object {
        fun emptyFilter(): ExcludeFilter {
            return object : ExcludeFilter() {
                override fun filter(path: Path): Boolean = false

                override fun isEmpty(): Boolean = true
            }
        }
    }
}