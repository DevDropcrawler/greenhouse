package nz.keeleysgreenhouse.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.ui.graphics.vector.ImageVector
import nz.keeleysgreenhouse.app.R

enum class TopDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Home(route = "home", labelRes = R.string.tab_home, icon = Icons.Outlined.Home),
    Calendar(route = "calendar", labelRes = R.string.tab_calendar, icon = Icons.Outlined.CalendarMonth),
    Crops(route = "crops", labelRes = R.string.tab_crops, icon = Icons.Outlined.Eco),
    Garden(route = "garden", labelRes = R.string.tab_garden, icon = Icons.Outlined.Yard),
    More(route = "more", labelRes = R.string.tab_more, icon = Icons.Outlined.MoreHoriz)
}
