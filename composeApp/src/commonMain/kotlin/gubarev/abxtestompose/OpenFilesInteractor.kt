package gubarev.abxtestompose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

private const val SAMPLE_URL_A = "https://gubarev.ru/abx_audio_samples/demo.aif"
private const val SAMPLE_URL_B = "https://gubarev.ru/abx_audio_samples/demo.mp3"
private const val SAMPLE_FILE_A = "demo.aif"
private const val SAMPLE_FILE_B = "demo.mp3"

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
    fun dispose()
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
            val mismatch = durA != null && durB != null && !durationsMatch(durA, durB)
            output?.didCheckDurationMismatch(mismatch)
        }
    }

    override fun loadSample() {
        loadSampleJob?.cancel()
        loadSampleJob = scope.launch {
            val pathA = downloadFile(SAMPLE_URL_A, SAMPLE_FILE_A)
            ensureActive()
            val pathB = downloadFile(SAMPLE_URL_B, SAMPLE_FILE_B)
            if (pathA != null && pathB != null) {
                output?.didLoadSample(pathA, pathB)
            }
        }
    }

    override fun cancelSampleLoad() {
        loadSampleJob?.cancel()
        loadSampleJob = null
    }

    override fun dispose() {
        output = null
        scope.cancel()
    }
}
