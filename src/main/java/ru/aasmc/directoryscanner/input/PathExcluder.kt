package ru.aasmc.directoryscanner.input

import ru.aasmc.directoryscanner.scan.filter.Filter
import java.nio.file.Path
import java.util.regex.Pattern

sealed class PathExcluder(open val key: String) {
    protected val excludeFiles: MutableList<Path> = mutableListOf()

    abstract fun excludePatterns(): List<Pattern>

    abstract fun createFilter(excludeFiles: List<Path>): Filter

    abstract fun validateAndAdd(param: String, vararg params: String)

    fun exclude(vararg params: String): Filter {
        var listParams = params.toList()

        if (!listParams.contains(key)) {
            return Filter.EmptyFilter
        }

        val keyIndex = listParams.indexOf(key)
        listParams = listParams.subList(keyIndex + 1, listParams.size)
        for (param in listParams) {
            if (param.startsWith("-")) break
            validateAndAdd(param, *params)
        }
        return createFilter(excludeFiles)
    }

    data class DirectoryPathExcluder(
        private val validator: PathValidator,
        override val key: String = "-"
    ) : PathExcluder(key) {

        override fun excludePatterns(): List<Pattern> = patterns

        override fun createFilter(excludeFiles: List<Path>): Filter {
            return if (excludeFiles.isEmpty()) {
                Filter.EmptyFilter
            } else {
                Filter.DirectoryFilter(dirsToFilter = excludeFiles, validator = validator)
            }
        }

        override fun validateAndAdd(param: String, vararg params: String) {
            val matchers = excludePatterns()
                .map { pattern -> pattern.matcher("") }

            validator.validateDirCorrect(
                path = param,
                params = params,
                matchers = matchers
            ) { dir ->
                excludeFiles.add(dir)
            }
        }

    }
}
