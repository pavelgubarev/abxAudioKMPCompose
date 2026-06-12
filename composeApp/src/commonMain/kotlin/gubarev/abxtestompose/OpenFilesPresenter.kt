package gubarev.abxtestompose

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

interface OpenFilesPresenterInterface {
    val state: StateFlow<OpenFilesState>
    fun pickFile(slot: FileSlot, path: String)
    fun loadSample()
    fun cancelSampleLoad()
    fun consumeSampleLoaded()
    fun dispose()
}

class OpenFilesPresenter private constructor(
    private val interactor: OpenFilesInteractorInterface
) : OpenFilesPresenterInterface, OpenFilesInteractorOutput {

    companion object {
        fun create(interactor: OpenFilesInteractorInterface = OpenFilesInteractor()): OpenFilesPresenter {
            val presenter = OpenFilesPresenter(interactor)
            interactor.output = presenter
            return presenter
        }
    }

    private val _state = MutableStateFlow(OpenFilesState())
    override val state: StateFlow<OpenFilesState> = _state.asStateFlow()

    override fun pickFile(slot: FileSlot, path: String) {
        _state.update {
            when (slot) {
                FileSlot.A -> it.copy(fileA = FilePickState.Validating, durationMismatch = false)
                FileSlot.B -> it.copy(fileB = FilePickState.Validating, durationMismatch = false)
            }
        }
        interactor.validateFile(slot, path)
    }

    override fun loadSample() {
        _state.update { it.copy(isLoadingSample = true) }
        interactor.loadSample()
    }

    override fun cancelSampleLoad() {
        interactor.cancelSampleLoad()
        _state.update { it.copy(isLoadingSample = false) }
    }

    override fun consumeSampleLoaded() {
        _state.update { it.copy(sampleLoaded = false) }
    }

    override fun dispose() {
        interactor.dispose()
    }

    // OpenFilesInteractorOutput

    override fun didValidateFile(slot: FileSlot, isValid: Boolean, path: String) {
        val newState = _state.updateAndGet {
            val newPickState = if (isValid) FilePickState.Valid(path) else FilePickState.Invalid
            when (slot) {
                FileSlot.A -> it.copy(fileA = newPickState)
                FileSlot.B -> it.copy(fileB = newPickState)
            }
        }
        val pathA = newState.pathA
        val pathB = newState.pathB
        if (pathA != null && pathB != null) {
            interactor.checkDurationMismatch(pathA, pathB)
        }
    }

    override fun didCheckDurationMismatch(mismatch: Boolean) {
        _state.update { it.copy(durationMismatch = mismatch) }
    }

    override fun didLoadSample(pathA: String, pathB: String) {
        _state.update {
            it.copy(
                fileA = FilePickState.Valid(pathA),
                fileB = FilePickState.Valid(pathB),
                durationMismatch = false,
                isLoadingSample = false,
                sampleLoaded = true
            )
        }
    }
}
