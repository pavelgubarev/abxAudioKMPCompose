package gubarev.abxtestompose

data class AudioMetadata(
    val bitrateKbps: Int?,
    val sampleRateHz: Int?,
    val bitDepthBits: Int?
) {
    fun displayString(): String = listOfNotNull(
        bitrateKbps?.let { "$it kbps" },
        sampleRateHz?.let { "$it Hz" },
        bitDepthBits?.let { "$it-bit" }
    ).joinToString(" · ")
}

expect fun getAudioMetadata(path: String): AudioMetadata
