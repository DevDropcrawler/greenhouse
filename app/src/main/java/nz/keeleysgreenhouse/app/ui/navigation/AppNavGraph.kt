package nz.keeleysgreenhouse.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import nz.keeleysgreenhouse.app.ui.calendar.CalendarScreen
import nz.keeleysgreenhouse.app.ui.crops.CropsScreen
import nz.keeleysgreenhouse.app.ui.garden.GardenScreen
import nz.keeleysgreenhouse.app.ui.home.HomeScreen
import nz.keeleysgreenhouse.app.ui.more.MoreScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = TopDestination.Home.route
    ) {
        composable(TopDestination.Home.route) { HomeScreen(contentPadding) }
        composable(TopDestination.Calendar.route) { CalendarScreen(contentPadding) }
        composable(TopDestination.Crops.route) { CropsScreen(contentPadding) }
        composable(TopDestination.Garden.route) { GardenScreen(contentPadding) }
        composable(TopDestination.More.route) { MoreScreen(contentPadding) }
    }
}
