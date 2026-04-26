package nz.keeleysgreenhouse.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.ui.navigation.AppNavGraph
import nz.keeleysgreenhouse.app.ui.navigation.GreenhouseBottomNav
import nz.keeleysgreenhouse.app.ui.navigation.SearchRoute
import nz.keeleysgreenhouse.app.ui.navigation.TopDestination
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss

private val TopLevelRoutesWithSearch = setOf(
    TopDestination.Home.route,
    TopDestination.Calendar.route,
    TopDestination.Crops.route,
    TopDestination.Garden.route
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenhouseApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showSearchAction = currentRoute in TopLevelRoutesWithSearch

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    if (showSearchAction) {
                        IconButton(onClick = { navController.navigate(SearchRoute) }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.search_action)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OliveMoss,
                    titleContentColor = Cream,
                    navigationIconContentColor = Cream,
                    actionIconContentColor = Cream
                )
            )
        },
        bottomBar = { GreenhouseBottomNav(navController) }
    ) { innerPadding ->
        AppNavGraph(navController = navController, contentPadding = innerPadding)
    }
}
