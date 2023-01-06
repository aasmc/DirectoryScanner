package ru.aasmc.directoryscanner.input

import ru.aasmc.directoryscanner.exceptions.ValidationParamsException
import java.nio.file.Path
import java.nio.file.Paths

class DefaultInputParamsParser(
    private val validator: DirectoryValidator
) : InputParser {

    private val dirsToScan = mutableListOf<Path>()

    private val excluders = mutableListOf<Excluder>()

    override fun registerExcluders(vararg excluders: Excluder) {
        this.excluders.addAll(excluders)
    }

    /**
     * Parses input parameters.
     * @return ParseResult that contains a list of directories to scan
     *         and a list of filters.
     */
    override fun parse(vararg params: String): ParseResult {
        val excludedKeys = excluders.map(Excluder::getKey)
        val matchers = patterns.map { pattern ->
            pattern.matcher("")
        }
        for (param in params) {
            if (excludedKeys.contains(param)) {
                // If the input parameter is a key, then the following parameter
                // must be handled by oe of the Excluder's,
                // so we break the search for directories to scan.
                break
            }
            val isDir = matchers.any { matcher -> matcher.reset(param).matches() }
            if (isDir) {
                val dir = Paths.get(param)
                if (!validator.doesExist(dir)) {
                    throw ValidationParamsException("Directory \"$param\" doesn't exist", params)
                }
                dirsToScan.add(dir)
            } else {
                throw ValidationParamsException(
                    """
                Input param \"$param\" has inappropriate format.
                It should be an absolute WINDOWS or UNIX DIRECTORY path, 
                i.e. it should end with "\" or "/".
                Example 1: C:\ProgramFiles\
                Example 2: /home/user/
                """.trimIndent(),
                    params
                )
            }
        }

        // search for files to be excl
        var excludeFilters = excluders
            .map { excluder ->
                excluder.exclude(*params)
            }
        // If there are any dirs among those we have found that are on the
        // list of to-be-filtered dirs we remove them
        val listIterator = dirsToScan.listIterator()
        while (listIterator.hasNext()) {
            val path = listIterator.next()
            val exclude = excludeFilters.any { f -> f.filter(path) }
            if (exclude) {
                listIterator.remove()
            }
        }
        // remove empty filters from the list
        excludeFilters = excludeFilters.filterNot { it.isEmpty() }
        return ParseResult(dirsToScan, excludeFilters)
    }
}