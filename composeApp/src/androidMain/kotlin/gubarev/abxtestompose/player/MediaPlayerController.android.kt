package gubarev.abxtestompose

import android.content.Context

actual class PlatformContext actual constructor() {
    val applicationContext: Context
        get() = appContext ?: error("Call PlatformContext.init(context) before use")

    companion object {
        private var appContext: Context? = null

        fun init(context: Context) {
            appContext = context.applicationContext
        }

        fun getContext(): Context? = appContext
    }
}