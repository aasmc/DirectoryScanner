package ru.aasmc.directoryscanner.exceptions

class InitException @JvmOverloads constructor(
    message: String? = null, throwable: Throwable? = null
) : RuntimeException(message, throwable) {
}