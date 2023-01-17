package ru.aasmc.directoryscanner.output

import java.nio.charset.StandardCharsets

class SortedFilesBuffer(
    private val maxSize: Int
) {
    private var countOfElements = 0

    var currentSize: Long = 0
        private set

    private var lines: MutableList<String> = mutableListOf()

    fun put(line: String): Long {
        val lineSize = line.toByteArray(StandardCharsets.UTF_8).size
        return if (currentSize + lineSize > maxSize) {
            -1
        } else {
            lines.add(line)
            countOfElements++
            currentSize += lineSize
            currentSize
        }
    }

    fun takeAll(): List<String> {
        val result = lines.toMutableList()
        lines = mutableListOf()
        currentSize = 0
        countOfElements = 0

        result.sortWith { l, r ->
            l.compareTo(r, ignoreCase = true)
        }
        return result
    }

}