package ru.aasmc.directoryscanner.output

import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class Timer {

    private val counter = AtomicInteger(0)
    private lateinit var scheduledExecutorService: ScheduledExecutorService
    private val timePrinting = TimePrinter()

    private var start = 0L

    fun start() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        scheduledExecutorService.scheduleAtFixedRate(timePrinting, 0, 1, TimeUnit.SECONDS)
        start = System.currentTimeMillis()
    }

    fun stop(): Long {
        if (Objects.nonNull(scheduledExecutorService)) {
            CompletableFuture.runAsync(scheduledExecutorService::shutdownNow)
        }
        return System.currentTimeMillis() - start
    }

    private inner class TimePrinter : Runnable {
        override fun run() {
            val times = counter.incrementAndGet()
            if (times % 60 == 0) {
                print("|")
            } else {
                print(".")
            }
        }
    }
}