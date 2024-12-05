package gubarev.abxtestompose

import kotlinx.serialization.Serializable

@Serializable
enum class NavigationScreens(val title: String) {
    Loader(title = "Loader"),
    Testing(title = "Testing")
}