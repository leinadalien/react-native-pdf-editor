package com.rnpdfeditorexample.thumbnail

import com.facebook.react.bridge.ReadableMap
import kotlin.jvm.Throws

data class DocumentsHandlerRequest(
    val documentsUrls: List<String>,
    val isGrayscale: Boolean,
    val expectedWidth: Float
) {
    companion object {

        @Throws(ParseException::class)
        fun fromReadableMap(map: ReadableMap): DocumentsHandlerRequest = try { DocumentsHandlerRequest(
            documentsUrls = map.getArray("documents")?.toArrayList()?.map { it as String }
                ?: throw Exception("Documents array is null!"),
            isGrayscale = map.getBoolean("grayscale"),
            expectedWidth = map.getDouble("expectedWidth").toFloat(),
        )
        } catch (t: Throwable) { throw ParseException(t) }
    }
}
