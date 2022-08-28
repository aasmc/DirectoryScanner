package ru.aasmc.directoryscanner.scan

import ru.aasmc.directoryscanner.exceptions.InitException
import ru.aasmc.directoryscanner.output.FileProcessor
import ru.aasmc.directoryscanner.scan.filter.Filter
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.RecursiveAction

class DirectoryScanning(
    private val dir: Path,
    private val dirExcludeFilters: List<Filter.DirectoryFilter>,
    private val fileExcludeFilters: List<Filter.FileFilter>
) : RecursiveAction() {

    private val processor = FileProcessor

    init {
        if (!processor.isStarted) {
            throw InitException("Cannot start scanning: File Processor hasn't been started yet")
        }
    }


    override fun compute() {
        val scanningList = ArrayList<DirectoryScanning>()
        try {
            Files.walkFileTree(dir, object : SimpleFileVisitor<Path>() {
                // before we start scanning the next directory this method is executed
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes?): FileVisitResult {
                    val skip = dirExcludeFilters.any { f -> f.filter(dir) }
                    if (skip) {
                        return FileVisitResult.SKIP_SUBTREE
                    }
                    // check if need to scan the directory
                    return if (dir != this@DirectoryScanning.dir) {
                        // if we don't need to scan, then create a sub-task for scanning
                        val w = DirectoryScanning(dir, dirExcludeFilters, fileExcludeFilters)
                        // start it in the same ForkJoinPool
                        w.fork()
                        scanningList.add(w)
                        FileVisitResult.SKIP_SUBTREE
                    } else {
                        // if this is our directory, then scan it
                        FileVisitResult.CONTINUE
                    }
                }

                override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                    val process = fileExcludeFilters.none { f -> f.filter(file) }
                    if (process) {
                        processor.process(file.toAbsolutePath().toString())
                    }
                    return FileVisitResult.CONTINUE
                }

                override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult {
                    return FileVisitResult.SKIP_SUBTREE
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for (dir in scanningList) {
            dir.join()
        }
    }
}