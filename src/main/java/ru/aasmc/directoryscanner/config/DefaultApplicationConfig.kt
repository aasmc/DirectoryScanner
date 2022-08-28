package ru.aasmc.directoryscanner.config

import ru.aasmc.directoryscanner.input.*
import ru.aasmc.directoryscanner.output.format.DefaultFileFormatter
import ru.aasmc.directoryscanner.output.format.FileFormatter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class DefaultApplicationConfig : ApplicationConfig {
    override fun outputFilePath(): Path {
        return Paths.get("./result.txt").toAbsolutePath().normalize()
    }

    override fun outputFileCharset(): Charset {
        return StandardCharsets.UTF_8
    }

    override fun fileFormatter(): FileFormatter {
        return DefaultFileFormatter(outputFilePath(), outputFileCharset())
    }

    override fun inputParamsExcluders(): List<Excluder> {
        return Collections.singletonList(DirectoryExcluder(SimplePathValidator()))
    }

    override fun inputParamsParser(): InputParser {
        return DefaultInputParamsParser(SimplePathValidator())
    }
}