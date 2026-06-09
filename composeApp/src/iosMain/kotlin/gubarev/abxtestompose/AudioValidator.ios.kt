package gubarev.abxtestompose

import platform.Foundation.NSURL
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeAudio

actual fun validateAudioFile(path: String): Boolean {
    val url = NSURL.URLWithString(URLString = path) ?: return false
    val ext = url.pathExtension?.takeIf { it.isNotEmpty() } ?: return false
    return UTType.typeWithFilenameExtension(
        filenameExtension = ext,
        conformingToType = UTTypeAudio
    ) != null
}
