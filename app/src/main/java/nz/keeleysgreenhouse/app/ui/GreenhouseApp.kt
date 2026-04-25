package nz.keeleysgreenhouse.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.ui.navigation.AppNavGraph
import nz.keeleysgreenhouse.app.ui.navigation.GreenhouseBottomNav
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenhouseApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
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
