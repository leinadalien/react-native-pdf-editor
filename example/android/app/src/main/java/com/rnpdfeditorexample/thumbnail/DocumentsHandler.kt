package com.rnpdfeditorexample.thumbnail

import android.content.Context
import android.os.Environment
import com.rnpdfeditorexample.thumbnail.document.Document
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import java.util.TreeSet

class DocumentsHandler(
    context: Context,
    simultaneouslyOperationCount: Int = DEFAULT_SIMULTANEOUSLY_OPERATION_COUNT
) {

    private val outputDirectory =
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
    private val semaphore = Semaphore(simultaneouslyOperationCount)
    private val operationScope = CoroutineScope(Dispatchers.Default)
    fun process(
        request: DocumentsHandlerRequest,
        onError: (t: Throwable) -> Unit = {},
        onSuccess: (DocumentsHandlerResult) -> Unit,
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            semaphore.release()
            onError(throwable)
        }
        operationScope.launch(exceptionHandler) {
            val jobs = mutableListOf<Job>()
            val results = TreeSet<Result>(compareBy { it.index })
            request.documentsUrls.forEachIndexed { index, url ->
                jobs.add(
                    operationScope.async(exceptionHandler) {
                        processDocument(
                            index,
                            url,
                            request.expectedWidth,
                            request.isGrayscale
                        )
                    }.also {
                        results.add(
                            Result(
                                index,
                                url,
                                it.await()
                            )
                        )
                    }
                )
            }
            jobs.joinAll()
            onSuccess(DocumentsHandlerResult(results.toList()))
        }
    }

    private suspend fun processDocument(
        index: Int,
        documentUrl: String,
        width: Float,
        isGrayscale: Boolean
    ): List<String> {
        val document = Document.fromUrl(index, documentUrl)
        val jobs = mutableListOf<Job>()
        val documentThumbnailUrls = TreeSet<Pair<Int, String>>(compareBy { it.first })
        for (pageIndex in 0 until document.pagesCount) {
            jobs.add(
                operationScope.async {
                    processDocumentThumbnail(document, pageIndex, width, isGrayscale)
                }.also {
                    documentThumbnailUrls.add(pageIndex to it.await())
                }
            )
        }
        jobs.joinAll()
        return documentThumbnailUrls.map { it.second }
    }

    private suspend fun processDocumentThumbnail(
        document: Document,
        pageIndex: Int,
        width: Float,
        isGrayscale: Boolean
    ): String {
        semaphore.acquire()
        val thumbnail= document.getThumbnail(
            outputDirectory!!,
            width,
            isGrayscale,
            pageIndex,
        )
        semaphore.release()
        return thumbnail
    }

    companion object {
        const val DEFAULT_SIMULTANEOUSLY_OPERATION_COUNT = 5
    }
}