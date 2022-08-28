package ru.aasmc.directoryscanner.input.validator

import ru.aasmc.directoryscanner.exceptions.ValidationParamsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher

class SimplePathValidator : PathValidator {

    override fun doesExist(path: Path): Boolean = Files.exists(path)

    override fun notExists(path: Path): Boolean {
        return !doesExist(path)
    }

    override fun isDirectory(path: Path): Boolean = Files.isDirectory(path)

    override fun isNotDirectory(path: Path): Boolean {

        return !isDirectory(path)
    }

    override fun isFile(path: Path): Boolean = Files.isRegularFile(path)

    override fun validateDirCorrect(
        path: String,
        params: Array<out String>,
        matchers: List<Matcher>,
        block: (p: Path) -> Unit
    ) {
        val isDir = matchers.any { matcher -> matcher.reset(path).matches() }
        if (isDir) {
            val dir = Paths.get(path)
            if (!doesExist(dir)) {
                throw ValidationParamsException("Directory \"$path\" doesn't exist", params)
            }
            block(dir)
        } else {
            throw ValidationParamsException(
                """
                Input param \"$path\" has inappropriate format.
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