package ru.aasmc.directoryscanner.sort

import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * Implements extended merge sort. All input data is sorted.
 */
class ExternalMergeSort {

    /**
     * Merges sorted files
     * @param files sorted files
     * @param outputFile output file
     * @param comparator comparator for strings
     * @param encoding encoding of input and output files
     * @return the number of lines written to the output file
     */
    fun mergeSortedFiles(
        files: List<File>,
        outputFile: File,
        comparator: Comparator<String>,
        encoding: Charset,
        append: Boolean
    ): Long {
        val bfbs = ArrayList<BinaryFileBuffer>(files.size)
        // create a wrapper for every file
        for (file in files) {
            val inStream = FileInputStream(file)
            val br = BufferedReader(InputStreamReader(inStream, encoding))
            val bfb = BinaryFileBuffer(br)
            bfbs.add(bfb)
        }

        // Writer that will write sorted strings to the output file
        val fbw =
            BufferedWriter(OutputStreamWriter(FileOutputStream(outputFile, append), encoding))
        val rowCounter = mergeSortedFiles(
            fbw = fbw,
            comparator = comparator,
            buffers = bfbs
        )

        for (file in files) {
            file.delete()
        }
        return rowCounter
    }

    /**
     * Merge sort implementation using PriorityQueue.
     * @param fbw - writer for the output file
     * @param comparator comparator for comparing strings
     * @param buffers wrappers for readers of sorted input files
     * @return the number of strings written to the output file
     */
    private fun mergeSortedFiles(
        fbw: BufferedWriter,
        comparator: Comparator<String>,
        buffers: List<BinaryFileBuffer>
    ): Long {
        // Queue of wrappers for readers with priority.
        // If a current string in the wrapper is lexicographically smaller
        // then it has a higher priority.

        val pq: PriorityQueue<BinaryFileBuffer> = PriorityQueue(
            11
        ) { first: BinaryFileBuffer, second: BinaryFileBuffer ->
            comparator.compare(first.peek(), second.peek())
        }

        // add all readers to the queue
        for (bfb in buffers) {
            if (!bfb.empty()) {
                pq.add(bfb)
            }
        }

        var rowCounter = 0L
        try {
            // main cycle of the merge
            while (pq.size > 0) {
                // retrieve the reader with the highest priority (the smallest current string)
                val bfb = pq.poll()
                // get current string from the reader
                // and read the next string from file
                // (next becomes current)
                val current = bfb.pop()
                // we retrieved the smallest string from all the files,
                // write it to the output file
                current?.let { cur ->
                    fbw.write(cur)
                }
                fbw.newLine()
                ++rowCounter
                if (bfb.empty()) {
                    bfb.close()
                } else {
                    pq.add(bfb)
                }
            }
        } finally {
            fbw.close()
            pq.forEach { bfb ->
                bfb.close()
            }
        }
        return rowCounter
    }
}