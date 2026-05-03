package nz.keeleysgreenhouse.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.ui.components.MonthCard
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Fraunces
import nz.keeleysgreenhouse.app.ui.theme.Inter
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import java.time.LocalDate

@Composable
fun CalendarScreen(
    contentPadding: PaddingValues,
    onMonthClick: (Int) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(contentPadding)
    ) {
        Spacer(Modifier.height(20.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "\u2726 BAY OF PLENTY \u00B7 WARM ZONE",
                color = Brass,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.calendar_title),
                color = OliveMoss,
                fontFamily = Fraunces,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp
            )
            Text(
                text = LocalDate.now().year.toString(),
                color = Brass,
                fontFamily = Fraunces,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp
            )
        }
        Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.months, key = { it.month }) { summary ->
                MonthCard(
                    month = summary.month,
                    seedCount = summary.seedCount,
                    seedlingCount = summary.seedlingCount,
                    harvestCount = summary.harvestCount,
                    isCurrent = summary.month == state.currentMonth,
                    onClick = { onMonthClick(summary.month) }
                )
            }
        }
    }
}
