package gubarev.abxtestompose

data class AudioSample(
    val url: String = "",
    val filename: String = ""
)

val audioSamples = Pair(
    AudioSample(
        url = "https://gubarev.ru/abx_audio_samples/Time-30.m4a",
        filename = "Time-30.m4a"
    ),
    AudioSample(
        url = "https://gubarev.ru/abx_audio_samples/Time-50.m4a",
        filename = "Time-50.m4a"
    )
)