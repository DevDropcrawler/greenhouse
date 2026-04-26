package nz.keeleysgreenhouse.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Task
import nz.keeleysgreenhouse.app.ui.components.EyebrowDivider
import nz.keeleysgreenhouse.app.ui.components.EyebrowLabel
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.Honey
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk
import nz.keeleysgreenhouse.app.ui.theme.Sprout
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    onCropClick: (Int) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(contentPadding),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Greeting(state) }

        if (state.todayTasks.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.home_today_tasks)) }
            items(state.todayTasks, key = { it.id }) { task ->
                TaskRow(task, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        item {
            CropSection(
                title = stringResource(R.string.home_sow_from_seed),
                accent = Forest,
                crops = state.sow,
                emptyText = stringResource(R.string.home_empty_sow),
                onCropClick = onCropClick
            )
        }
        item {
            CropSection(
                title = stringResource(R.string.home_transplant),
                accent = Sprout,
                crops = state.transplant,
                emptyText = stringResource(R.string.home_empty_transplant),
                onCropClick = onCropClick
            )
        }
        item {
            CropSection(
                title = stringResource(R.string.home_harvest),
                accent = Honey,
                crops = state.harvest,
                emptyText = stringResource(R.string.home_empty_harvest),
                onCropClick = onCropClick
            )
        }
    }
}

@Composable
private fun Greeting(state: HomeUiState) {
    val hour = LocalTime.now().hour
    val greetingRes = when {
        hour < 12 -> R.string.home_greeting_morning
        hour < 18 -> R.string.home_greeting_afternoon
        else -> R.string.home_greeting_evening
    }
    val dateText = state.today.format(
        DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(greetingRes),
            color = OliveMoss,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = dateText,
            color = OnSurfaceInk,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        EyebrowLabel(title)
        Spacer(Modifier.height(8.dp))
        EyebrowDivider()
    }
}

@Composable
private fun CropSection(
    title: String,
    accent: Color,
    crops: List<Crop>,
    emptyText: String,
    onCropClick: (Int) -> Unit = {}
) {
    Column {
        SectionHeader(title)
        Spacer(Modifier.height(12.dp))
        if (crops.isEmpty()) {
            Text(
                text = emptyText,
                color = OnSurfaceInk.copy(alpha = 0.55f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(crops, key = { it.id }) { crop ->
                    CropPosterCard(crop = crop, accent = accent, onClick = { onCropClick(crop.id) })
                }
            }
        }
    }
}

@Composable
private fun CropPosterCard(crop: Crop, accent: Color, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.width(160.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(accent.copy(alpha = 0.18f))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(accent)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = crop.category.name.take(2),
                        color = Cream,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = crop.commonName,
                    color = OnSurfaceInk,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = crop.family,
                    color = OnSurfaceInk.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TaskRow(task: Task, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Surface(
                shape = CircleShape,
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brass),
                modifier = Modifier.size(22.dp)
            ) {}
            Spacer(Modifier.width(16.dp))
            Text(
                text = task.title,
                color = OnSurfaceInk,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
