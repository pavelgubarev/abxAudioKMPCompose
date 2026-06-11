package gubarev.abxtestompose

import android.media.MediaMetadataRetriever
import android.net.Uri

actual fun getAudioDuration(path: String): Double? {
    val context = PlatformContext.getContext() ?: return null
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(path))
        val ms = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull()
        retriever.release()
        ms?.div(1000.0)
    } catch (_: Exception) { null }
}
