package gubarev.abxtestompose

import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

fun MainViewController() = ComposeUIViewController {
//    val presenter = LoaderPresenter()
//
//    LoaderView(presenter)
//

    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationScreens.Loader.title,
    ) {
        composable(route = NavigationScreens.Loader.title) {
            LoaderView(presenter = LoaderPresenter(), onNavigateToTesting = { navController.navigate(route = NavigationScreens.Testing.title) })
        }
        composable(route = NavigationScreens.Testing.title) {
            App(presenter = Presenter())
        }
    }

}