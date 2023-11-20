package com.rnpdfeditorexample.thumbnail

import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class ThumbnailModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {
    override fun getName() = "RNDocumentsHandler"

    @ReactMethod
    fun process(data: ReadableMap, promise: Promise) {
        Log.d("THUMBNAIL_METHOD", "process started")
        val request = try {
            DocumentsHandlerRequest.fromReadableMap(data)
        } catch (ex: ParseException) {
            promise.reject(ex)
            return
        }
        val handler = DocumentsHandler(reactContext)
        handler.process(request,
            onError = {
                Log.d("THUMBNAIL_METHOD", "process ended with error:\n$it")
                promise.reject(it) },
            onSuccess = {
                Log.d("THUMBNAIL_METHOD", "process ended")
                promise.resolve(it.writeToMap())
            }
        )
    }

}