package gubarev.abxtestompose

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import platform.AVFAudio.AVAudioFile
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSError
import platform.Foundation.NSURL

// ALAC format ID = 'alac' as OSType
private const val kAudioFormatAppleLossless: UInt = 0x616c6163u

@OptIn(ExperimentalForeignApi::class)
actual fun getAudioMetadata(path: String): AudioMetadata {
    val url = NSURL.URLWithString(URLString = path) ?: return AudioMetadata(null, null, null)
    url.startAccessingSecurityScopedResource()

    var sampleRateHz: Int? = null
    var bitrateKbps: Int? = null
    var bitDepthBits: Int? = null

    try {
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()

            val audioFile = AVAudioFile(forReading = url, error = errorPtr.ptr)
                ?: return@memScoped
            val sampleRate = audioFile.fileFormat.sampleRate
            sampleRateHz = sampleRate.toInt()

            // Read bit depth from AudioStreamBasicDescription
            val asbd = audioFile.fileFormat.streamDescription?.pointed
            if (asbd != null) {
                val bitsPerChannel = asbd.mBitsPerChannel.toInt()
                bitDepthBits = when {
                    // Linear PCM (WAV, AIFF, CAF): bit depth is in mBitsPerChannel
                    bitsPerChannel > 0 -> bitsPerChannel
                    // ALAC: source bit depth is encoded in mFormatFlags
                    asbd.mFormatID == kAudioFormatAppleLossless -> when (asbd.mFormatFlags) {
                        1u -> 16
                        2u -> 20
                        3u -> 24
                        4u -> 32
                        else -> null
                    }
                    else -> null
                }
            }

            val filePath = url.path ?: return@memScoped
            val attrs = NSFileManager.defaultManager
                .attributesOfItemAtPath(filePath, error = errorPtr.ptr)
            val fileSizeBytes = (attrs?.get("NSFileSize") as? NSNumber)?.longValue
                ?: return@memScoped
            val durationSeconds = audioFile.length.toDouble() / sampleRate
            if (durationSeconds > 0) {
                bitrateKbps = ((fileSizeBytes * 8L).toDouble() / durationSeconds / 1000.0).toInt()
            }
        }
    } catch (_: Throwable) {}

    return AudioMetadata(bitrateKbps, sampleRateHz, bitDepthBits)
}
