package gubarev.abxtestompose

data class OpenFilesState(
    val fileA: FilePickState = FilePickState.None,
    val fileB: FilePickState = FilePickState.None,
    val durationMismatch: Boolean = false,
    val isLoadingSample: Boolean = false
) {
    val pathA: String? get() = (fileA as? FilePickState.Valid)?.path
    val pathB: String? get() = (fileB as? FilePickState.Valid)?.path
    val canLoad: Boolean get() = pathA != null && pathB != null && !durationMismatch
}

sealed interface FilePickState {
    data object None : FilePickState
    data object Validating : FilePickState
    data class Valid(val path: String) : FilePickState
    data object Invalid : FilePickState
}

enum class FileSlot { A, B }
