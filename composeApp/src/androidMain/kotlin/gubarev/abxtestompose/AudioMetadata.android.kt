package gubarev.abxtestompose

import android.media.AudioFormat
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build

actual fun getAudioMetadata(path: String): AudioMetadata {
    val context = PlatformContext.getContext() ?: return AudioMetadata(null, null, null)
    val uri = Uri.parse(path)

    var bitrateKbps: Int? = null
    var sampleRateHz: Int? = null
    var bitDepthBits: Int? = null

    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        bitrateKbps = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
            ?.toLongOrNull()
            ?.div(1000)
            ?.toInt()
        retriever.release()
    } catch (_: Exception) {}

    try {
        val extractor = MediaExtractor()
        extractor.setDataSource(context, uri, null)
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (!mime.startsWith("audio/")) continue

            if (format.containsKey(MediaFormat.KEY_SAMPLE_RATE))
                sampleRateHz = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)

            if (format.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
                val enc = format.getInteger(MediaFormat.KEY_PCM_ENCODING)
                bitDepthBits = when (enc) {
                    AudioFormat.ENCODING_PCM_8BIT -> 8
                    AudioFormat.ENCODING_PCM_16BIT -> 16
                    AudioFormat.ENCODING_PCM_FLOAT -> 32
                    else -> if (Build.VERSION.SDK_INT >= 31) when (enc) {
                        AudioFormat.ENCODING_PCM_24BIT_PACKED -> 24
                        AudioFormat.ENCODING_PCM_32BIT -> 32
                        else -> null
                    } else null
                }
            }
            break
        }
        extractor.release()
    } catch (_: Exception) {}

    return AudioMetadata(bitrateKbps, sampleRateHz, bitDepthBits)
}
