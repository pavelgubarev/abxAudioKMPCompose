package gubarev.abxtestompose

import android.content.Context
import java.io.File

actual class FileHandler(private val context: Context) {
    actual suspend fun saveFile(data: ByteArray, fileName: String): String {
        val file = File(context.cacheDir, fileName)
        file.writeBytes(data)
        return file.absolutePath
    }
}