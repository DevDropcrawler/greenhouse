package nz.keeleysgreenhouse.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import nz.keeleysgreenhouse.app.ui.calendar.CalendarScreen
import nz.keeleysgreenhouse.app.ui.calendar.MonthDetailScreen
import nz.keeleysgreenhouse.app.ui.crops.CropDetailScreen
import nz.keeleysgreenhouse.app.ui.crops.CropsScreen
import nz.keeleysgreenhouse.app.ui.diseases.DiseaseDetailScreen
import nz.keeleysgreenhouse.app.ui.diseases.DiseasesScreen
import nz.keeleysgreenhouse.app.ui.garden.GardenScreen
import nz.keeleysgreenhouse.app.ui.home.HomeScreen
import nz.keeleysgreenhouse.app.ui.more.MoreScreen
import nz.keeleysgreenhouse.app.ui.pests.PestDetailScreen
import nz.keeleysgreenhouse.app.ui.pests.PestsScreen
import nz.keeleysgreenhouse.app.ui.search.SearchScreen

const val CropDetailRoute = "crop"
const val CropDetailArg = "cropId"
const val MonthDetailRoute = "month"
const val MonthDetailArg = "month"
const val PestsRoute = "pests"
const val PestDetailRoute = "pest"
const val PestDetailArg = "pestId"
const val DiseasesRoute = "diseases"
const val DiseaseDetailRoute = "disease"
const val DiseaseDetailArg = "diseaseId"
const val SearchRoute = "search"

fun cropDetailRoute(cropId: Int) = "$CropDetailRoute/$cropId"
fun monthDetailRoute(month: Int) = "$MonthDetailRoute/$month"
fun pestDetailRoute(pestId: Int) = "$PestDetailRoute/$pestId"
fun diseaseDetailRoute(diseaseId: Int) = "$DiseaseDetailRoute/$diseaseId"

@Composable
fun AppNavGraph(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = TopDestination.Home.route
    ) {
        composable(TopDestination.Home.route) {
            HomeScreen(
                contentPadding = contentPadding,
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) }
            )
        }
        composable(TopDestination.Calendar.route) {
            CalendarScreen(
                contentPadding = contentPadding,
                onMonthClick = { m -> navController.navigate(monthDetailRoute(m)) }
            )
        }
        composable(TopDestination.Crops.route) {
            CropsScreen(
                contentPadding = contentPadding,
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) }
            )
        }
        composable(TopDestination.Garden.route) { GardenScreen(contentPadding) }
        composable(TopDestination.More.route) {
            MoreScreen(
                contentPadding = contentPadding,
                onSearchClick = { navController.navigate(SearchRoute) },
                onPestsClick = { navController.navigate(PestsRoute) },
                onDiseasesClick = { navController.navigate(DiseasesRoute) }
            )
        }
        composable(SearchRoute) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) },
                onPestClick = { id -> navController.navigate(pestDetailRoute(id)) },
                onDiseaseClick = { id -> navController.navigate(diseaseDetailRoute(id)) }
            )
        }
        composable(
            route = "$CropDetailRoute/{$CropDetailArg}",
            arguments = listOf(navArgument(CropDetailArg) { type = NavType.IntType })
        ) {
            CropDetailScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = "$MonthDetailRoute/{$MonthDetailArg}",
            arguments = listOf(navArgument(MonthDetailArg) { type = NavType.IntType })
        ) {
            MonthDetailScreen(
                onBack = { navController.popBackStack() },
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) }
            )
        }
        composable(PestsRoute) {
            PestsScreen(
                onBack = { navController.popBackStack() },
                onPestClick = { id -> navController.navigate(pestDetailRoute(id)) }
            )
        }
        composable(
            route = "$PestDetailRoute/{$PestDetailArg}",
            arguments = listOf(navArgument(PestDetailArg) { type = NavType.IntType })
        ) {
            PestDetailScreen(
                onBack = { navController.popBackStack() },
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) }
            )
        }
        composable(DiseasesRoute) {
            DiseasesScreen(
                onBack = { navController.popBackStack() },
                onDiseaseClick = { id -> navController.navigate(diseaseDetailRoute(id)) }
            )
        }
        composable(
            route = "$DiseaseDetailRoute/{$DiseaseDetailArg}",
            arguments = listOf(navArgument(DiseaseDetailArg) { type = NavType.IntType })
        ) {
            DiseaseDetailScreen(
                onBack = { navController.popBackStack() },
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) }
            )
        }
    }
}
