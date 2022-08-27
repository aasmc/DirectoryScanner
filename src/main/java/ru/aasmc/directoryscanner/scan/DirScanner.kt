package ru.aasmc.directoryscanner.scan

import ru.aasmc.directoryscanner.exceptions.InitException
import ru.aasmc.directoryscanner.input.ParseResult
import ru.aasmc.directoryscanner.output.FileProcessor
import ru.aasmc.directoryscanner.scan.filter.DirectoryExcludeFilter
import ru.aasmc.directoryscanner.scan.filter.ExcludeFilter
import ru.aasmc.directoryscanner.scan.filter.FileExcludeFilter
import java.nio.file.Path
import java.util.concurrent.ForkJoinPool

object DirScanner {
    private var isInit = false
    private lateinit var dirForScan: List<Path>

    private val dirExcludeFilters = mutableListOf<DirectoryExcludeFilter>()
    private val fileExcludeFilters = mutableListOf<FileExcludeFilter>()

    private val scanPool = ForkJoinPool(Runtime.getRuntime().availableProcessors())

    fun init(result: ParseResult) {
        registerFilters(result.filters)
        this.dirForScan = result.dirsToScan
        isInit = true
    }

    private fun registerFilters(filters: List<ExcludeFilter>) {
        filters.forEach(::registerFilter)
    }

    private fun registerFilter(filter: ExcludeFilter) {
        if (filter.isEmpty()) return
        when (filter) {
            is DirectoryExcludeFilter -> {
                dirExcludeFilters.add(filter)
            }
            is FileExcludeFilter -> {
                fileExcludeFilters.add(filter)
            }
            else -> {
                throw IllegalArgumentException("[Dir Scanner] Error while registering filter. Filter has unknown type.")
            }
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