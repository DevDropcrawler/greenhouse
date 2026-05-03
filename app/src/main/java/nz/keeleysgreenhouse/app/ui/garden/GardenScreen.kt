package nz.keeleysgreenhouse.app.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.ui.components.EyebrowLabel
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.Honey
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk
import nz.keeleysgreenhouse.app.ui.theme.Sprout
import nz.keeleysgreenhouse.app.ui.theme.Terracotta
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun GardenScreen(
    contentPadding: PaddingValues,
    onAddClick: () -> Unit = {},
    onPlantingClick: (Long) -> Unit = {},
    viewModel: GardenViewModel = hiltViewModel()
) {
    val rows by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(contentPadding)
    ) {
        if (rows.isEmpty()) {
            EmptyState(onAddClick = onAddClick)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 96.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.garden_title),
                        color = OliveMoss,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                }
                items(rows, key = { it.planting.id }) { row ->
                    PlantingCard(row = row, onClick = { onPlantingClick(row.planting.id) })
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = Forest,
            contentColor = Cream,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.garden_add))
        }
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        EyebrowLabel(text = stringResource(R.string.garden_onboarding_eyebrow))
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.garden_onboarding_title),
            color = OliveMoss,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )
        Spacer(Modifier.height(20.dp))
        OnboardingStep(
            number = 1,
            title = stringResource(R.string.garden_onboarding_step1_title),
            body = stringResource(R.string.garden_onboarding_step1_body)
        )
        Spacer(Modifier.height(14.dp))
        OnboardingStep(
            number = 2,
            title = stringResource(R.string.garden_onboarding_step2_title),
            body = stringResource(R.string.garden_onboarding_step2_body)
        )
        Spacer(Modifier.height(14.dp))
        OnboardingStep(
            number = 3,
            title = stringResource(R.string.garden_onboarding_step3_title),
            body = stringResource(R.string.garden_onboarding_step3_body)
        )
        Spacer(Modifier.height(28.dp))
        ExtendedFloatingActionButton(
            onClick = onAddClick,
            containerColor = Forest,
            contentColor = Cream,
            text = { Text(stringResource(R.string.garden_add)) },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun OnboardingStep(number: Int, title: String, body: String) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Brass.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = Brass,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = OnSurfaceInk,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = body,
                color = OnSurfaceInk.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PlantingCard(row: PlantingRow, onClick: () -> Unit) {
    val accent = stageColor(row.stage)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            EyebrowLabel(text = stageLabel(row.stage))
            Spacer(Modifier.height(6.dp))
            Text(
                text = row.crop.commonName,
                color = OnSurfaceInk,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = row.planting.location.ifBlank { row.crop.family },
                color = OnSurfaceInk.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { row.progress.coerceIn(0f, 1f) },
                color = accent,
                trackColor = accent.copy(alpha = 0.18f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Spacer(Modifier.height(8.dp))
            val sown = java.time.LocalDate.parse(row.planting.sownDate)
                .format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
            val harvestStart = java.time.LocalDate.parse(row.planting.expectedHarvestStart)
                .format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
            Text(
                text = "Sown $sown · Harvest $harvestStart",
                color = Brass,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun stageLabel(stage: PlantingStage): String = when (stage) {
    PlantingStage.SEED -> stringResource(R.string.garden_stage_seed)
    PlantingStage.SEEDLING -> stringResource(R.string.garden_stage_seedling)
    PlantingStage.GROWING -> stringResource(R.string.garden_stage_growing)
    PlantingStage.HARVEST -> stringResource(R.string.garden_stage_harvest)
}

private fun stageColor(stage: PlantingStage): Color = when (stage) {
    PlantingStage.SEED -> Honey
    PlantingStage.SEEDLING -> Terracotta
    PlantingStage.GROWING -> Forest
    PlantingStage.HARVEST -> Sprout
}
