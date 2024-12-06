package gubarev.abxtestompose

import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

fun MainViewController() = ComposeUIViewController {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Loader,
    ) {
        composable(route = Loader.toString()) {
            LoaderView(presenter = LoaderPresenter(), onNavigateToTesting = { tracksFiles ->
                navController.navigate(route = Testing(tracksFiles))
            })
        }
        composable(route = Testing.toString()) {
            val presenter = ABXTestingPresenter()
            ABXTestingView(presenter = presenter)
        }
    }
}