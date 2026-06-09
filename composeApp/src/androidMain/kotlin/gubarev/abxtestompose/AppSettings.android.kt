package gubarev.abxtestompose

import android.content.Context

actual class AppSettings actual constructor() {
    private val prefs = PlatformContext.getContext()
        ?.getSharedPreferences("abx_prefs", Context.MODE_PRIVATE)

    actual fun get(key: String): String? = prefs?.getString(key, null)
    actual fun set(key: String, value: String) {
        prefs?.edit()?.putString(key, value)?.apply()
    }
}
