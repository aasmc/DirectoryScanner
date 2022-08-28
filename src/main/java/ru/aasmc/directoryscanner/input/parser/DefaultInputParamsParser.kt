package ru.aasmc.directoryscanner.input.parser

import ru.aasmc.directoryscanner.input.excluder.PathExcluder
import ru.aasmc.directoryscanner.input.validator.PathValidator
import ru.aasmc.directoryscanner.input.validator.patterns
import ru.aasmc.directoryscanner.scan.filter.Filter
import java.nio.file.Path

class DefaultInputParamsParser(
    private val validator: PathValidator
) : InputParser {

    private val dirsToScan = mutableListOf<Path>()

    private val excluders = mutableListOf<PathExcluder>()

    override fun registerExcluders(vararg excluders: PathExcluder) {
        this.excluders.addAll(excluders)
    }

    /**
     * Parses input parameters.
     * @return ParseResult that contains a list of directories to scan
     *         and a list of filters.
     */
    override fun parse(vararg params: String): ParseResult {
        val excludedKeys = excluders.map(PathExcluder::key)
        val matchers = patterns.map { pattern ->
            pattern.matcher("")
        }
        for (param in params) {
            if (excludedKeys.contains(param)) {
                // If the input parameter is a key, then the following parameter
                // must be handled by oe of the Excluder's,
                // so we bread the search for directories to scan.
                break
            }

            validator.validateDirCorrect(
                path = param,
                params = params,
                matchers = matchers
            ) { dir ->
                dirsToScan.add(dir)
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
        excludeFilters = excludeFilters.filterNot { it is Filter.EmptyFilter }
        return ParseResult(dirsToScan, excludeFilters)
    }
}