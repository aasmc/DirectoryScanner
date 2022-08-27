package ru.aasmc.directoryscanner

import ru.aasmc.directoryscanner.config.DefaultApplicationConfig

fun main(args: Array<String>) {
    val application = Application
    val config = DefaultApplicationConfig()
    application.init(config)
    application.start(*args)
}