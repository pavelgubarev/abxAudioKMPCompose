package gubarev.abxtestompose

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import java.io.File

actual val defaultPlatformEngine: HttpClient = HttpClient(CIO)

actual class FileHandler {
    actual suspend fun saveFile(data: ByteArray, fileName: String): String {
//        val file = File(context.cacheDir, fileName)
//        file.writeBytes(data)
//        return file.absolutePath
        return ""
    }
}