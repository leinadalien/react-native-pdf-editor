package com.rnpdfeditorexample.thumbnail

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

data class DocumentsHandlerResult(
    val results: List<Result>
) {
    fun writeToMap(): WritableMap = Arguments.createMap().apply {
        putArray("result", Arguments.createArray().apply {
            results.forEach { pushMap(it.writeToMap()) }
        })
    }

}
