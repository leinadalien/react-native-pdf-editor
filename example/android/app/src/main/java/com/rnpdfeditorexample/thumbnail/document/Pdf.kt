package com.rnpdfeditorexample.thumbnail.document

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.ceil
import kotlin.math.sqrt

data class Pdf(
    override val index: Int,
    override val documentUrl: String
) : Document() {
    private val rendersPool = RendersPool(File(documentUrl))
    override val pagesCount: Int = withReader { pageCount }.also {
        rendersPool.capacity = ceil(sqrt(it.toFloat())).toInt()
    }

    override fun getThumbnail(outputDirectory: String, width: Float, isGrayscale: Boolean, pageIndex: Int) : String {
        var bitmap = render(pageIndex, width)
        if (isGrayscale) {
            bitmap = makeBitmapGrayscale(bitmap)
        }
        val name = File(documentUrl).nameWithoutExtension
        val outputName = "$outputDirectory/$name-page-$pageIndex-thumbnail.jpg"
        saveBitmap(bitmap, outputName)
        return outputName
    }

    private fun render(pageIndex: Int, width: Float): Bitmap {
        return withReader {
            val page = openPage(pageIndex)
            val ratio = page.width.toFloat() / page.height
            val pageBitmap =
                Bitmap.createBitmap(width.toInt(), (width / ratio).toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(pageBitmap)
            canvas.drawColor(Color.WHITE)
            page.render(pageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pageBitmap
        }
    }

    private fun <R> withReader(action: PdfRenderer.() -> R) : R {
        val renderer = rendersPool.acquire()
        val result = renderer.action()
        rendersPool.release(renderer)
        return result
    }


    private class RendersPool(private val pdfFile: File) {
        var capacity: Int = DEFAULT_RENDERS_CAPACITY
        private fun createRenderer() = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
        private val pool: BlockingQueue<PdfRenderer> = LinkedBlockingQueue()

        init {
            for (i in 0 until capacity) {
                pool.offer(createRenderer())
            }
        }

        fun acquire() = pool.poll() ?: createRenderer()


        fun release(renderer: PdfRenderer) {
            if (pool.size < capacity) {
                pool.offer(renderer)
            }
        }
        companion object {
            const val DEFAULT_RENDERS_CAPACITY = 5
        }
    }
}


