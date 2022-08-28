package ru.aasmc.directoryscanner.scan

import ru.aasmc.directoryscanner.exceptions.InitException
import ru.aasmc.directoryscanner.input.parser.ParseResult
import ru.aasmc.directoryscanner.output.FileProcessor
import ru.aasmc.directoryscanner.scan.filter.Filter
import java.nio.file.Path
import java.util.concurrent.ForkJoinPool

object DirScanner {
    private var isInit = false
    private lateinit var dirForScan: List<Path>

    private val dirExcludeFilters = mutableListOf<Filter.DirectoryFilter>()
    private val fileExcludeFilters = mutableListOf<Filter.FileFilter>()

    private val scanPool = ForkJoinPool(Runtime.getRuntime().availableProcessors())

    fun init(result: ParseResult) {
        registerFilters(result.filters)
        this.dirForScan = result.dirsToScan
        isInit = true
    }

    private fun registerFilters(filters: List<Filter>) {
        filters.forEach(::registerFilter)
    }

    private fun registerFilter(filter: Filter) {

        when (filter) {
            is Filter.DirectoryFilter -> {
                dirExcludeFilters.add(filter)
            }
            is Filter.FileFilter -> {
                fileExcludeFilters.add(filter)
            }
            is Filter.EmptyFilter -> return
        }
    }

    fun scan() {
        if (!isInit) {
            throw InitException("Cannot start scanning: Dir Scanner is not initialized.")
        }
        // For every directory we create task DirectoryScanning and put it into
        // ForkJoinPool where it is executed.
        dirForScan.map { dir ->
            DirectoryScanning(dir, dirExcludeFilters, fileExcludeFilters)
        }
            .forEach(scanPool::invoke)

        // After scanning we signal to FileProcessor that scanning has completed.
        FileProcessor.finish()
    }
}