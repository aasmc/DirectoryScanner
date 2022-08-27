package ru.aasmc.directoryscanner.input

import ru.aasmc.directoryscanner.scan.filter.ExcludeFilter
import java.nio.file.Path
import java.util.regex.Pattern

/**
 * Abstract class responsible for excluding files from scanning.
 * Default implementation used by the app - {@link DirectoryExcluder}
 */
abstract class Excluder {

    /**
     * List of files to be excluded from scanning process.
     */
    protected val excludeFiles: MutableList<Path> = mutableListOf()

    /**
     * Returns the key followed by files to be excluded and handled by the Excluder.
     *
     * @return a string, preferably the one that starts with '-', e.g. -key
     */
    abstract fun getKey(): String

    /**
     * Returns the list of regex patterns to be used for finding files that are valid
     * for this Excluder.
     */
    abstract fun excludePatterns(): List<Pattern>

    /**
     * Creates a filter to be used during scanning for filtering files, which
     * were found in the input parameters.
     */
    abstract fun createFilter(excludeFiles: List<Path>): ExcludeFilter

    /**
     * Validates every input parameter. Validation is performed according to the
     * regex Patterns from method {@link Excluder#excludePatterns()}
     *
     * @param param input parameter for validation
     * @param params list of all input parameters
     */
    protected abstract fun validateAndAdd(param: String, vararg params: String)

    /**
     * Contains common logic of validating input parameters.
     * Returns an instance of ExcludeFilter corresponding to this Excluder.
     */
    fun exclude(vararg params: String): ExcludeFilter {
        var listParams = params.toList()
        // If the list of parameters doesn't contain a key for this Excluder
        // then create an empty filter on an empty list of files
        if (!listParams.contains(getKey())) {
            return createFilter(excludeFiles)
        }

        // get the list of all parameters after the key
        val keyIndex = listParams.indexOf(getKey())
        listParams = listParams.subList(keyIndex + 1, listParams.size)
        for (param in listParams) {
            if (param.startsWith("-")) {
                // if the parameter starts with "-" then it is the beginning of the
                // next key, and all params for this Excluder have been handled
                break
            }
            // validate the parameter and add it to the list of files to be excluded if success
            validateAndAdd(param, *params)
        }
        return createFilter(excludeFiles)
    }
}
