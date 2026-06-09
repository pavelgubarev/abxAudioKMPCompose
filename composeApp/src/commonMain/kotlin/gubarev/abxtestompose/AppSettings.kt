package gubarev.abxtestompose

expect class AppSettings() {
    fun get(key: String): String?
    fun set(key: String, value: String)
}
