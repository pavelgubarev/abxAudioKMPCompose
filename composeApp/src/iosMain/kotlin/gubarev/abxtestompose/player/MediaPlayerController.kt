package gubarev.abxtestompose

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.isPlaybackLikelyToKeepUp
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.currentTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.CoreMedia.CMTimeConvertScale
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSEC_PER_SEC
import kotlin.experimental.ExperimentalNativeApi

import abxtestcompose.composeapp.generated.resources.Res
import kotlinx.cinterop.useContents
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.AVFoundation.duration
import kotlin.native.ref.WeakReference

@OptIn(ExperimentalForeignApi::class)
actual class MediaPlayerController actual constructor(val platformContext: PlatformContext) {

    private lateinit var timeObserver: Any

    private val player: AVPlayer = AVPlayer()

    private var listener: MediaPlayerListener? = null

    var code: TrackCode = TrackCode.A

    @OptIn(ExperimentalNativeApi::class)
    private var delegate: WeakReference<PresenterInterface>? = null

    private var cmtimeStruct: CValue<CMTime> = CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt())

    init {
        setUpAudioSession()
    }

    @OptIn(ExperimentalResourceApi::class, ExperimentalNativeApi::class)
    actual fun prepare(pathSource: String, listener: MediaPlayerListener, delegate: PresenterInterface, code: TrackCode) {
        this.code = code
        this.listener = listener
        this.delegate = WeakReference(delegate)
//        val url = NSURL(string = pathSource)
        startTimeObserver()
//        player.replaceCurrentItemWithPlayerItem(AVPlayerItem(url))

        val item = Res.getUri(pathSource)
        val itemURL =  NSURL.URLWithString(URLString = item)
        player.replaceCurrentItemWithPlayerItem(itemURL?.let { AVPlayerItem(it) })
    }

    private fun setUpAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            println("Error setting up audio session: ${e.message}")
        }
    }


    @OptIn(ExperimentalNativeApi::class)
    private val observer: (CValue<CMTime>) -> Unit = {
        delegate?.get()?.didTimeChangeTo(time = getCurrentTime(), code = this.code)
        if (player.currentItem?.isPlaybackLikelyToKeepUp() == true) {
            listener?.onReady()
        }
    }

    actual fun getCurrentTime(): Double {
        return  CMTimeConvertScale(player.currentTime(), cmtimeStruct.useContents { this.timescale }.toInt(), method = 1u ).useContents { this.value }.toDouble()
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = player.addPeriodicTimeObserverForInterval(interval, null, observer)
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                listener?.onAudioCompleted()
            }
        )
    }

    actual fun start() {
        player.play()
    }

    actual fun pause() {
        player.pause()
    }

    actual fun syncTo(progress: Double) {

        // заменить на copy?
        val newTime: CValue<CMTime> = cValue{
            value = progress.toLong()
            epoch = cmtimeStruct.useContents { this.epoch }
            flags = cmtimeStruct.useContents { this.flags }
            timescale = cmtimeStruct.useContents { this.timescale }
        }

        player.seekToTime(newTime)
    }

    actual fun isPlaying(): Boolean {
        return this.player.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    actual fun release() {
        observer.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
    }

    actual fun duration(): Double {
        player.currentItem?.also { currentItem ->
            cmtimeStruct = currentItem.duration
            return currentItem.duration().useContents { this.value }.toDouble()
        }
        return 0.0
    }
}