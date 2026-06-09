package gubarev.abxtestompose

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeAudio
import platform.darwin.NSObject

@Composable
actual fun FilePickerButton(label: String, onFilePicked: (String) -> Unit) {
    val delegate = remember { DocumentPickerDelegate() }
    delegate.callback = onFilePicked

    Button(onClick = {
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypeAudio),
            asCopy = false
        )
        picker.delegate = delegate
        picker.allowsMultipleSelection = false

        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootVC?.presentViewController(picker, animated = true, completion = null)
    }) {
        Text(label)
    }
}

private class DocumentPickerDelegate : NSObject(), UIDocumentPickerDelegateProtocol {
    var callback: ((String) -> Unit)? = null

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
        url.startAccessingSecurityScopedResource()
        url.absoluteString?.let { callback?.invoke(it) }
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {}
}
