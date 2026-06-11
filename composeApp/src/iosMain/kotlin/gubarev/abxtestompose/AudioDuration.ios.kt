package gubarev.abxtestompose

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.AVFAudio.AVAudioFile
import platform.Foundation.NSError
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
actual fun getAudioDuration(path: String): Double? {
    val url = NSURL.URLWithString(URLString = path) ?: return null
    url.startAccessingSecurityScopedResource()
    return try {
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            val audioFile = AVAudioFile(forReading = url, error = errorPtr.ptr) ?: return null
            val sampleRate = audioFile.fileFormat.sampleRate
            if (sampleRate <= 0.0) return null
            audioFile.length.toDouble() / sampleRate
        }
    } catch (_: Throwable) { null }
}
