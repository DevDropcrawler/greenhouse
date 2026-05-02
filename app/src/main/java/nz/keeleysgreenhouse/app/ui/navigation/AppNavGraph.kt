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
import nz.keeleysgreenhouse.app.ui.favourites.FavouritesScreen
import nz.keeleysgreenhouse.app.ui.garden.AddPlantingScreen
import nz.keeleysgreenhouse.app.ui.garden.GardenScreen
import nz.keeleysgreenhouse.app.ui.garden.PlantingDetailScreen
import nz.keeleysgreenhouse.app.ui.home.HomeScreen
import nz.keeleysgreenhouse.app.ui.more.MoreScreen
import nz.keeleysgreenhouse.app.ui.pests.PestDetailScreen
import nz.keeleysgreenhouse.app.ui.pests.PestsScreen
import nz.keeleysgreenhouse.app.ui.search.SearchScreen
import nz.keeleysgreenhouse.app.ui.settings.SettingsScreen
import nz.keeleysgreenhouse.app.ui.tasks.TasksScreen

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
const val AddPlantingRoute = "add_planting"
const val AddPlantingArg = "cropId"
const val PlantingDetailRoute = "planting"
const val PlantingDetailArg = "plantingId"
const val TasksRoute = "tasks"
const val SettingsRoute = "settings"
const val FavouritesRoute = "favourites"

fun addPlantingRoute(cropId: Int? = null): String =
    if (cropId == null) AddPlantingRoute else "$AddPlantingRoute?$AddPlantingArg=$cropId"

fun cropDetailRoute(cropId: Int) = "$CropDetailRoute/$cropId"
fun monthDetailRoute(month: Int) = "$MonthDetailRoute/$month"
fun pestDetailRoute(pestId: Int) = "$PestDetailRoute/$pestId"
fun diseaseDetailRoute(diseaseId: Int) = "$DiseaseDetailRoute/$diseaseId"
fun plantingDetailRoute(plantingId: Long) = "$PlantingDetailRoute/$plantingId"

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
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) },
                onTasksClick = { navController.navigate(TasksRoute) }
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
        composable(TopDestination.Garden.route) {
            GardenScreen(
                contentPadding = contentPadding,
                onAddClick = { navController.navigate(AddPlantingRoute) },
                onPlantingClick = { id -> navController.navigate(plantingDetailRoute(id)) }
            )
        }
        composable(TopDestination.More.route) {
            MoreScreen(
                contentPadding = contentPadding,
                onSearchClick = { navController.navigate(SearchRoute) },
                onPestsClick = { navController.navigate(PestsRoute) },
                onDiseasesClick = { navController.navigate(DiseasesRoute) },
                onTasksClick = { navController.navigate(TasksRoute) },
                onFavouritesClick = { navController.navigate(FavouritesRoute) },
                onSettingsClick = { navController.navigate(SettingsRoute) }
            )
        }
        composable(FavouritesRoute) {
            FavouritesScreen(
                onBack = { navController.popBackStack() },
                onCropClick = { id -> navController.navigate(cropDetailRoute(id)) },
                onPestClick = { id -> navController.navigate(pestDetailRoute(id)) },
                onDiseaseClick = { id -> navController.navigate(diseaseDetailRoute(id)) }
            )
        }
        composable(SettingsRoute) {
            SettingsScreen(onBack = { navController.popBackStack() })
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
            route = "$AddPlantingRoute?$AddPlantingArg={$AddPlantingArg}",
            arguments = listOf(navArgument(AddPlantingArg) {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            AddPlantingScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(
            route = "$PlantingDetailRoute/{$PlantingDetailArg}",
            arguments = listOf(navArgument(PlantingDetailArg) { type = NavType.LongType })
        ) {
            PlantingDetailScreen(onBack = { navController.popBackStack() })
        }
        composable(TasksRoute) {
            TasksScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = "$CropDetailRoute/{$CropDetailArg}",
            arguments = listOf(navArgument(CropDetailArg) { type = NavType.IntType })
        ) {
            CropDetailScreen(
                onBack = { navController.popBackStack() },
                onAddToGarden = { id -> navController.navigate(addPlantingRoute(id)) }
            )
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
