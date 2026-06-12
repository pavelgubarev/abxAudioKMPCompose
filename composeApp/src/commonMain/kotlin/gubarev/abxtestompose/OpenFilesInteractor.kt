package gubarev.abxtestompose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val MAX_DURATION_DIFF_SECONDS = 1.0

interface OpenFilesInteractorOutput {
    fun didValidateFile(slot: FileSlot, isValid: Boolean, path: String)
    fun didCheckDurationMismatch(mismatch: Boolean)
    fun didLoadSample(pathA: String, pathB: String)
}

interface OpenFilesInteractorInterface {
    var output: OpenFilesInteractorOutput?
    fun validateFile(slot: FileSlot, path: String)
    fun checkDurationMismatch(pathA: String, pathB: String)
    fun loadSample()
    fun cancelSampleLoad()
}

class OpenFilesInteractor : OpenFilesInteractorInterface {

    override var output: OpenFilesInteractorOutput? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loadSampleJob: Job? = null

    override fun validateFile(slot: FileSlot, path: String) {
        scope.launch {
            val valid = validateAudioFile(path)
            output?.didValidateFile(slot, valid, path)
        }
    }

    override fun checkDurationMismatch(pathA: String, pathB: String) {
        scope.launch {
            val durA = getAudioDuration(pathA)
            val durB = getAudioDuration(pathB)
            val mismatch = if (durA != null && durB != null) abs(durA - durB) > MAX_DURATION_DIFF_SECONDS else false
            output?.didCheckDurationMismatch(mismatch)
        }
    }

    override fun loadSample() {
        loadSampleJob?.cancel()
        loadSampleJob = scope.launch {
            val pathA = downloadFile("https://gubarev.ru/abx_audio_samples/demo.aif", "demo.aif")
            ensureActive()
            val pathB = downloadFile("https://gubarev.ru/abx_audio_samples/demo.mp3", "demo.mp3")
            if (pathA != null && pathB != null) {
                output?.didLoadSample(pathA, pathB)
            }
        }
    }

    override fun cancelSampleLoad() {
        loadSampleJob?.cancel()
        loadSampleJob = null
    }
}
