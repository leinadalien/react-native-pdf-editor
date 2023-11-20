package com.rnpdfeditorexample.thumbnail.document

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import java.io.File
import java.io.FileOutputStream

sealed class Document {
    abstract val index: Int
    abstract val documentUrl: String
    abstract val pagesCount: Int

    abstract fun getThumbnail(outputDirectory: String, width: Float, isGrayscale: Boolean, pageIndex: Int = 0) : String

    protected fun makeBitmapGrayscale(bitmap: Bitmap): Bitmap {
        val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val colorMatrix = ColorMatrix().apply { setSaturation(0f) }
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(colorMatrix) }
        Canvas(resultBitmap).apply {
            drawBitmap(bitmap, 0f, 0f, paint)
        }
        return resultBitmap
    }

    protected fun saveBitmap(bitmap: Bitmap, fileName: String) {
        val file = File(fileName).apply {
            if (!exists()) createNewFile()
        }
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            bitmap.recycle()
        }
    }


    companion object {
        fun fromUrl(index: Int, documentUrl: String) =
            when (val ext = documentUrl.substringAfterLast('.', "").uppercase()) {
                "JPG", "JPEG", "PNG" -> Image(index, documentUrl)
                "PDF" -> Pdf(index, documentUrl)
                else -> throw Exception("Not compatible file extension '$ext'")
            }
    }
}