package com.rnpdfeditorexample.thumbnail

class ParseException(causedException: Throwable): Exception() {
    override val message = "Parsing failed with:\n${causedException.message}"
}