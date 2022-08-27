package ru.aasmc.directoryscanner.exceptions

class ValidationParamsException (
    message: String,
    private val params: Array<out String>,
    e: Throwable? = null
) : RuntimeException(e) {

    private val PREFIX: String = "[Validation params ERROR]: "

    override val message: String = "$PREFIX ${paramsString()} $message"

    private fun paramsString(): String {
        return params.joinToString(
            separator = " ",
            prefix = "Input params string: \"",
            postfix = "\"\n\t"
        )
    }
}