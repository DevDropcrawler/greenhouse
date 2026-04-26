package nz.keeleysgreenhouse.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.ui.components.MonthCard
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk
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
                text = stringResource(R.string.calendar_title),
                color = OliveMoss,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = LocalDate.now().year.toString(),
                color = OnSurfaceInk.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
