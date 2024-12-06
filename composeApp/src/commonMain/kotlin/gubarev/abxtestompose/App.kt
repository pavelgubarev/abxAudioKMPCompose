package gubarev.abxtestompose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun app() {
    val navController: NavHostController = rememberNavController()

    var tracksToTest: TracksToTest = mapOf()

    NavHost(
        navController = navController,
        startDestination = Loader,
    ) {
        composable<Loader> {
            LoaderView(presenter = LoaderPresenter(), onNavigateToTesting = { tracksFiles ->
                tracksToTest = tracksFiles
                navController.navigate(route = Testing)
            })
        }
        composable<Testing> {
            val presenter = ABXTestingPresenter()
            presenter.configure(
                tracksToTest = tracksToTest
            )
            ABXTestingView(presenter = presenter)
        }
    }
}