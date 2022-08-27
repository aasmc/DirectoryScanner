package ru.aasmc.directoryscanner

import ru.aasmc.directoryscanner.config.ApplicationConfig
import ru.aasmc.directoryscanner.exceptions.InitException
import ru.aasmc.directoryscanner.exceptions.ValidationParamsException
import ru.aasmc.directoryscanner.input.InputParser
import ru.aasmc.directoryscanner.output.FileProcessor
import ru.aasmc.directoryscanner.output.Timer
import ru.aasmc.directoryscanner.scan.DirScanner
import java.nio.file.Files

object Application {

    private lateinit var config: ApplicationConfig

    private var isInit = false

    private val timer = Timer()

    private val scanner: DirScanner = DirScanner

    private lateinit var paramsParser: InputParser

    private val processor: FileProcessor = FileProcessor

    fun init(config: ApplicationConfig) {
        this.config = config

        paramsParser = config.inputParamsParser()
        paramsParser.registerExcluders(*config.inputParamsExcluders().toTypedArray())
        processor.init(config)

        isInit = true
    }

    fun start(vararg inputParams: String) {
        try {
            println("Application started")
            timer.start()
            if (!isInit) {
                throw InitException("Cannot start application: application is not initialized")
            }
            Files.deleteIfExists(config.outputFilePath())
            processor.start()
            val result = paramsParser.parse(*inputParams)
            scanner.init(result)
            scanner.scan()
            processor.waitForComplete()
        } catch (e: ValidationParamsException) {
            println(e.message)
            println("Shutting the application down.")
        } catch (e: InitException) {
            println(e.message)
            println("Shutting the application down.")
        } catch (e: Exception) {
            println("Uexpected error occurred:")
            e.printStackTrace()
        } finally {
            val execTimeMs = timer.stop()
            println("\nApplication finished: execution time = ${execTimeMs}ms")
            shutdown()
        }
    }

    private fun shutdown() {
        processor.finish()
    }
}