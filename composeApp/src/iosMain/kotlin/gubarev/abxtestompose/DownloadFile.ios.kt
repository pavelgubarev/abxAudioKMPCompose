package gubarev.abxtestompose

import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToFile

actual suspend fun downloadFile(url: String, fileName: String): String? {
    return try {
        val nsUrl = NSURL.URLWithString(url) ?: return null
        val data = NSData.dataWithContentsOfURL(nsUrl) ?: return null
        val filePath = NSTemporaryDirectory() + fileName
        if (data.writeToFile(filePath, atomically = true)) "file://$filePath" else null
    } catch (_: Throwable) { null }
}
