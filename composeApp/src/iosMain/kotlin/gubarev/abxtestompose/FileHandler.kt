package gubarev.abxtestompose

import io.ktor.client.HttpClient
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.*

import io.ktor.client.engine.darwin.*

actual val defaultPlatformEngine: HttpClient = HttpClient(Darwin)

actual class FileHandler actual constructor() {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun saveFile(data: ByteArray, fileName: String): String {
        val fileManager = NSFileManager.defaultManager
        val cacheDir = fileManager.URLsForDirectory(NSCachesDirectory, NSUserDomainMask).first() as NSURL
        val fileURL = cacheDir.URLByAppendingPathComponent(fileName)

        fileURL?.path?.let { path ->
            val file = fopen(path, "wb")
            if (file != null) {
                data.usePinned { pinned ->
                    fwrite(pinned.addressOf(0), 1u, data.size.toULong(), file)
                }
            }
            fclose(file)
        }

        return fileURL?.absoluteString ?: ""
    }
}