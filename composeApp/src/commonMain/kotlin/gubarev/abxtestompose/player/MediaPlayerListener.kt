package gubarev.abxtestompose

interface MediaPlayerListener {
    fun onReady()
    fun onAudioCompleted()
    fun onError()
}

interface MediaPlayerDelegate {
    fun didTimeChangeTo(time: Double, code: TrackCode)
}
