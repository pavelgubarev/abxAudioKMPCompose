package gubarev.abxtestompose

import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.media3.exoplayer.ExoPlayer

actual class MediaPlayerController actual constructor(platformContext: PlatformContext) {
    private val player = ExoPlayer.Builder(platformContext.applicationContext).build()
    private val handler = Handler(Looper.getMainLooper())
    private var delegate: PresenterInterface? = null
    private var trackCode: TrackCode = TrackCode.A

    private val timeUpdater = object : Runnable {
        override fun run() {
            delegate?.didTimeChangeTo(getCurrentTime(), trackCode)
            handler.postDelayed(this, 500)
        }
    }

    actual fun getCurrentTime(): Double = player.currentPosition.toDouble()

    actual fun prepare(pathSource: String, listener: MediaPlayerListener, delegate: PresenterInterface, code: TrackCode) {
        this.delegate = delegate
        this.trackCode = code
        val mediaItem = MediaItem.fromUri(pathSource)
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                listener.onError()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    STATE_READY -> listener.onReady()
                    STATE_ENDED -> listener.onAudioCompleted()
                }
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                listener.onError()
            }
        })
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        handler.post(timeUpdater)
    }

    actual fun start() {
        player.play()
    }

    actual fun pause() {
        if (player.isPlaying) player.pause()
    }

    actual fun release() {
        handler.removeCallbacks(timeUpdater)
        player.release()
    }

    actual fun isPlaying(): Boolean = player.isPlaying

    actual fun syncTo(progress: Double) {
        player.seekTo(progress.toLong())
    }

    actual fun duration(): Double = player.duration.toDouble()
}
