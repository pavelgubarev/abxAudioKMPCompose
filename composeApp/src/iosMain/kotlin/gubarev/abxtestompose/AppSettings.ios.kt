package gubarev.abxtestompose

import platform.Foundation.NSUserDefaults

actual class AppSettings actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun get(key: String): String? = defaults.stringForKey(key)
    actual fun set(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }
}
