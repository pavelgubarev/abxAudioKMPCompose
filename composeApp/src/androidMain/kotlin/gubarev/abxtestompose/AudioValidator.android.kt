package gubarev.abxtestompose

import android.media.MediaMetadataRetriever
import android.net.Uri

actual fun validateAudioFile(path: String): Boolean {
    return try {
        val context = PlatformContext.getContext() ?: return false
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(path))
        val hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) == "yes"
        retriever.release()
        hasAudio
    } catch (e: Exception) {
        false
    }
}
