package com.rnpdfeditorexample.thumbnail.document

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File

data class Image(
    override val index: Int,
    override val documentUrl: String
) : Document() {
    override val pagesCount = 1

    override fun getThumbnail(outputDirectory: String, width: Float, isGrayscale: Boolean, pageIndex: Int): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(documentUrl, options)
        val scale = width / options.outWidth
        options.inJustDecodeBounds = false
        options.inSampleSize = scale.toInt()
        val bitmap = BitmapFactory.decodeFile(documentUrl, options)
        val matrix = Matrix().apply {
            postScale(scale, scale)
        }
        var resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        if(isGrayscale) {
            resizedBitmap = makeBitmapGrayscale(resizedBitmap)
        }
        val name = File(documentUrl).nameWithoutExtension
        val outputName = "$outputDirectory/$name-thumbnail.jpg"
        saveBitmap(resizedBitmap, outputName)
        return outputName
    }
}