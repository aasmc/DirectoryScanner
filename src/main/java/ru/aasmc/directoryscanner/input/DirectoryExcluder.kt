package ru.aasmc.directoryscanner.input

import ru.aasmc.directoryscanner.exceptions.ValidationParamsException
import ru.aasmc.directoryscanner.scan.filter.DefaultDirectoryExcludeFilter
import ru.aasmc.directoryscanner.scan.filter.ExcludeFilter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

class DirectoryExcluder(
    private val validator: PathValidator
) : Excluder() {

    private val KEY: String = "-"

    override fun getKey(): String = KEY

    override fun excludePatterns(): List<Pattern> {
        return patterns
    }

    override fun createFilter(excludeFiles: List<Path>): ExcludeFilter {
        return if (excludeFiles.isEmpty()) {
            ExcludeFilter.emptyFilter()
        } else {
            DefaultDirectoryExcludeFilter(excludeFiles, validator)
        }
    }

    override fun validateAndAdd(param: String, vararg params: String) {
        val matchers = excludePatterns()
            .map { pattern -> pattern.matcher("") }
        val isDir = matchers.any { matcher -> matcher.reset(param).matches() }
        if (isDir) { // input parameter is a valid absolute path to a directory
            val dir = Paths.get(param)
            if (!validator.doesExist(dir)) {
                throw ValidationParamsException("Directory \"$param\" doesn't exist", params)
            }
            excludeFiles.add(dir)
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

}