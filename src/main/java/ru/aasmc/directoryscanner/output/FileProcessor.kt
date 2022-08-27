package ru.aasmc.directoryscanner.output

import ru.aasmc.directoryscanner.config.ApplicationConfig
import ru.aasmc.directoryscanner.exceptions.InitException
import ru.aasmc.directoryscanner.sort.ExternalMergeSort
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.LinkedBlockingQueue

object FileProcessor {
    @Volatile
    var isStarted = false
        private set

    var isInit = false
        private set

    private val queueFilter = ForkJoinPool(Runtime.getRuntime().availableProcessors())

    private lateinit var writerManagerThread: Thread

    private lateinit var writeManager: WriteManager

    fun init(config: ApplicationConfig) {
        writeManager = WriteManager(config)
        writerManagerThread = Thread(writeManager)
        writerManagerThread.name = "WriteManager Thread"
        isInit = true
    }

    fun start() {
        if (!isInit) {
            throw InitException("Cannot start File Processor: File Processor is not initialized")
        }
        writerManagerThread.start()
        isStarted = true
    }

    fun process(fileName: String) {
        val task = { writeManager.putOutputEntry(fileName) }
        CompletableFuture.runAsync(task, queueFilter)
    }

    fun finish() {
        // wait till the thread pool that is responsible for filling the queue has no active threads
        // and that the queue is empty and then send signal to WriterManager
        while (queueFilter.hasQueuedSubmissions() || queueFilter.activeThreadCount > 0 ||
            writeManager.filesQueue.size > 0
        ) {
            Thread.yield()
        }
        writerManagerThread.interrupt()
    }

    fun waitForComplete() {
        while (isStarted) {
            Thread.yield()
        }
    }

    private class WriteManager(
        private val config: ApplicationConfig
    ) : Runnable {

        private var flushCounter = 0

        /**
         * Since there can be a lot of files, we need a buffer to store
         * the number of files that can be squeezed into RAM.
         *
         * The size is set in ApplicationConfig
         */
        private val buffer: SortedFilesBuffer = SortedFilesBuffer(config.outputEntryBufferSize())

        /**
         * The list of sorted files, whose size doesn't exceed the max size of the buffer.
         */
        private val sortedFiles = ArrayList<File>()

        /**
         * Blocking queue that stores pathes to files.
         */
        val filesQueue = LinkedBlockingQueue<String>()

        override fun run() {
            while (true) {
                try {
                    // take the next file from the queue (a string containing absolute path)
                    // we block if the queue is empty
                    val file = filesQueue.take()
                    if (buffer.put(file) < 0) {
                        // if the buffer is full
                        // flush it to the temporary sorted file
                        flushBuffer()
                        buffer.put(file)
                    }
                } catch (e: InterruptedException) {
                    finish()
                    return
                }
            }
        }

        /**
         * It is called when the scanning is finished.
         * This is a signal that no more files are added to the queue.
         */
        private fun finish() {
            try {
                while (true) {
                    val file = filesQueue.poll()
                    if (file == null) {
                        // when we find null in the queue (i.e. the queue is empty)
                        // we create a file to store all alphabetically sorted entries
                        val sortedFile = Paths.get("${config.outputFilePath().toString()}_sorted")
                        Files.deleteIfExists(sortedFile)
                        if (buffer.currentSize > 0) {
                            // if there's something in the buffer
                            // flush the remainings into a temporary sorted file
                            flushBuffer()
                        }

                        if (flushCounter == 1) {
                            // if we flushed the buffer only 1 time
                            // i.e we have only one temporary sorted file
                            // it is called NameOfOutputFile_0
                            val tmp0 = "${config.outputFilePath().toString()}_0"
                            Files.move(Paths.get(tmp0), sortedFile)
                        } else {
                            // if we have several temporary sorted files
                            // then we need to merge them preserving the alphabetic order
                            performSortingTo(sortedFile)
                        }
                        val fileFormatter = config.fileFormatter()
                        fileFormatter.format(sortedFile)
                        return
                    } else {
                        if (buffer.put(file) < 0) {
                            flushBuffer()
                            buffer.put(file)
                        }
                    }
                }
            } finally {
                isStarted = false
            }
        }

        /**
         * Method that combines several sorted files into one
         * according to the extended merge sort algorithm.
         *
         * @param sortedFile - file that will store the result
         */
        private fun performSortingTo(sortedFile: Path) {
            val sort = ExternalMergeSort()
            sort.mergeSortedFiles(
                sortedFiles,
                sortedFile.toAbsolutePath().toFile(),
                Comparator.comparing(String::lowercase),
                config.outputFileCharset(),
                true
            )
        }

        /**
         * Flushes the buffer and creates a temporary sorted file.
         */
        private fun flushBuffer() {
            var filePath = config.outputFilePath().toString()
            filePath = "${filePath}_$flushCounter"
            flushCounter++
            val file = Paths.get(filePath)
            sortedFiles.add(file.toFile())
            val toFlush = buffer.takeAll()
            flushToFile(file, toFlush)
        }

        private fun flushToFile(file: Path, toFlush: List<String>) {
            val writer = FileWriter(file, StandardCharsets.UTF_8)
            writer.writelnLines(toFlush)
        }

        fun putOutputEntry(fileName: String) {
            try {
                filesQueue.put(fileName)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

    }
}