package com.rnpdfeditorexample.thumbnail

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

data class Result(
    val index: Int,
    val incomingUrl: String,
    val outgoingUrls: List<String>
) {
    fun writeToMap(): WritableMap = Arguments.createMap().apply {
        putInt("index", index)
        putString("incoming", incomingUrl)
        putArray("outcoming", Arguments.createArray().apply {
            outgoingUrls.forEach { pushString(it) }
        })
    }
}
