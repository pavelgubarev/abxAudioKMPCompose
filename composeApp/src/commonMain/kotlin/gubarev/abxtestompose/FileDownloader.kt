package gubarev.abxtestompose

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.contentLength
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext


expect val defaultPlatformEngine: HttpClient

expect class FileHandler() {
    suspend fun saveFile(data: ByteArray, fileName: String): String
}

class ByteArrayBuilder {
    private val buffer = mutableListOf<Byte>()

    fun write(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size) {
        buffer.addAll(bytes.sliceArray(offset until (offset + length)).toList())
    }

    fun toByteArray(): ByteArray = buffer.toByteArray()
}


class FileDownloader(private val httpClient: HttpClient = HttpClient()) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default

    suspend fun downloadFile(model: DownloaderModel): String? {

        return withContext(ioDispatcher) {
            try {
                val response: HttpResponse = httpClient.get(model.url)
                val totalBytes = (response.contentLength() ?: 0L).toFloat()
                val byteArray = ByteArrayBuilder()
                val channel: ByteReadChannel = response.bodyAsChannel()

                var bytesRead = 0f
                val buffer = ByteArray(8 * 1024)

                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer)
                    if (read > 0) {
                        byteArray.write(buffer, 0, read)
                        bytesRead += read
                        model.progress.value = if (totalBytes > 0f) (bytesRead / totalBytes) else 0f
                    }
                }

                model.handler.saveFile(byteArray.toByteArray(), model.fileName)

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}