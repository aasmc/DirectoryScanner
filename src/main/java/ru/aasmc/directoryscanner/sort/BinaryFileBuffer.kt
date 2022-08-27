package ru.aasmc.directoryscanner.sort

import java.io.BufferedReader

/**
 * This is a wrapper for BufferedReader
 */
class BinaryFileBuffer(
    val fbr: BufferedReader
) {
    private var cache: String? = null

    init {
        reload()
    }

    fun close() {
        fbr.close()
    }

    fun empty(): Boolean = cache == null

    fun peek(): String? = cache

    fun pop(): String? {
        val answer = peek()
        reload()
        return answer
    }

    private fun reload() {
        cache = fbr.readLine()
    }
}