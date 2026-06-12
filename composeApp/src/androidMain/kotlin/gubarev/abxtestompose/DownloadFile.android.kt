package gubarev.abxtestompose

import java.io.File
import java.net.URL

actual suspend fun downloadFile(url: String, fileName: String): String? {
    val context = PlatformContext.getContext() ?: return null
    return try {
        val file = File(context.cacheDir, fileName)
        URL(url).openStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.toURI().toString()
    } catch (_: Throwable) { null }
}
