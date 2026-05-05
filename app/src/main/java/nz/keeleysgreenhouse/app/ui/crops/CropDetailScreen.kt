package nz.keeleysgreenhouse.app.ui.crops

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.domain.usecase.CropDetail
import nz.keeleysgreenhouse.app.ui.components.AccordionSection
import nz.keeleysgreenhouse.app.ui.components.VideoSection
import nz.keeleysgreenhouse.app.ui.components.Fact
import nz.keeleysgreenhouse.app.ui.components.FactsGrid
import nz.keeleysgreenhouse.app.ui.components.TimelineBar
import nz.keeleysgreenhouse.app.ui.components.TimelineLegend
import nz.keeleysgreenhouse.app.ui.theme.Sprout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropDetailScreen(
    onBack: () -> Unit,
    onAddToGarden: (Int) -> Unit,
    viewModel: CropDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isFavourite by viewModel.isFavourite.collectAsState()
    val cropId = state.detail?.crop?.id

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.detail?.crop?.commonName ?: "Crop") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (cropId != null) {
                        IconButton(onClick = { viewModel.toggleFavourite() }) {
                            Icon(
                                imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites"
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
        bottomBar = {
            if (cropId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Cream)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { onAddToGarden(cropId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Forest, contentColor = Cream),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Add to garden")
                    }
                }
            }
        },
        containerColor = Cream
    ) { inner ->
        when {
            state.loading -> Box(
                modifier = Modifier.fillMaxSize().padding(inner),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = OliveMoss) }

            state.detail == null -> Box(
                modifier = Modifier.fillMaxSize().padding(inner),
                contentAlignment = Alignment.Center
            ) { Text("Crop not found", color = OnSurfaceInk) }

            else -> CropDetailContent(detail = state.detail!!, contentPadding = inner)
        }
    }
}

@Composable
private fun CropDetailContent(detail: CropDetail, contentPadding: PaddingValues) {
    val crop = detail.crop
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        val ctx = LocalContext.current
        val heroId = remember(crop.imageResName) {
            ctx.resources.getIdentifier(crop.imageResName, "drawable", ctx.packageName)
        }
        if (heroId != 0) {
            Image(
                painter = painterResource(heroId),
                contentDescription = crop.commonName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brass.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = crop.commonName.take(1).uppercase(),
                    color = OliveMoss,
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            Text(
                text = crop.commonName,
                color = OliveMoss,
                style = MaterialTheme.typography.displayMedium
            )
            crop.maoriName?.let {
                Text(
                    text = it,
                    color = OnSurfaceInk.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic
                )
            }
            Text(
                text = crop.family,
                color = OnSurfaceInk.copy(alpha = 0.55f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "\uD83C\uDF3F  Greenhouse",
                color = Forest,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(6.dp))
            TimelineBar(
                sowMonths = crop.seedSowMonths,
                transplantMonths = crop.seedlingPlantMonths,
                harvestMonths = crop.harvestMonths,
                showMonthLabels = crop.outdoorSowMonths.isEmpty()
            )
            if (crop.outdoorSowMonths.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "\u2600\uFE0F  Outdoors",
                    color = OnSurfaceInk.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(6.dp))
                TimelineBar(
                    sowMonths = crop.outdoorSowMonths,
                    transplantMonths = crop.outdoorSeedlingMonths,
                    harvestMonths = crop.outdoorHarvestMonths,
                    alpha = 0.6f
                )
            }
            Spacer(Modifier.height(8.dp))
            TimelineLegend()
            Spacer(Modifier.height(20.dp))
            FactsGrid(facts = factsOf(crop))
            Spacer(Modifier.height(12.dp))

            AccordionSection(title = "Sowing", initiallyExpanded = true) {
                BodyText(crop.sowingNotes)
            }
            crop.growingNotes.takeIf { it.isNotBlank() }?.let { notes ->
                AccordionSection(title = "Growing", initiallyExpanded = true) {
                    BodyText(notes)
                }
            }
            crop.greenhouseNotes.takeIf { it.isNotBlank() }?.let { notes ->
                AccordionSection(title = "Growing in the greenhouse", initiallyExpanded = true) {
                    BodyText(notes)
                }
            }
            crop.outdoorNotes.takeIf { it.isNotBlank() }?.let { notes ->
                AccordionSection(title = "Outdoor growing") {
                    BodyText(notes)
                }
            }
            AccordionSection(title = "Feeding") {
                BodyText(crop.feedingNotes)
            }
            crop.pruningNotes?.takeIf { it.isNotBlank() }?.let { notes ->
                AccordionSection(title = "Pruning") { BodyText(notes) }
            }
            crop.pollinationNotes?.takeIf { it.isNotBlank() }?.let { notes ->
                AccordionSection(title = "Pollination") { BodyText(notes) }
            }
            if (detail.companions.isNotEmpty() || detail.avoid.isNotEmpty()) {
                AccordionSection(title = "Companions") {
                    if (detail.companions.isNotEmpty()) {
                        BodyText("Plant with: " + detail.companions.joinToString { it.commonName })
                    }
                    if (detail.avoid.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        BodyText("Avoid near: " + detail.avoid.joinToString { it.commonName })
                    }
                }
            }
            if (detail.pests.isNotEmpty()) {
                AccordionSection(title = "Pests") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        detail.pests.forEach { p -> PestRow(p) }
                    }
                }
            }
            if (detail.diseases.isNotEmpty()) {
                AccordionSection(title = "Diseases") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        detail.diseases.forEach { d -> DiseaseRow(d) }
                    }
                }
            }
            if (crop.youtubeVideoIds.isNotEmpty() || crop.youtubeSearchQuery.isNotBlank()) {
                AccordionSection(title = "Videos", initiallyExpanded = true) {
                    VideoSection(
                        videoIds = crop.youtubeVideoIds,
                        searchQuery = crop.youtubeSearchQuery
                    )
                }
            }
        }
    }
}

private fun factsOf(crop: Crop): List<Fact> = buildList {
    add(Fact("Spacing", "${crop.spacingCm} cm"))
    crop.rowSpacingCm?.let { add(Fact("Row spacing", "$it cm")) }
    crop.sowDepthMm?.let { add(Fact("Sow depth", "$it mm")) }
    add(Fact("Day temp", "${crop.dayTempC.first}–${crop.dayTempC.last} °C"))
    add(Fact("Night temp", "${crop.nightTempC.first}–${crop.nightTempC.last} °C"))
    add(Fact("Humidity", "${crop.humidityPct.first}–${crop.humidityPct.last} %"))
    add(Fact("Sun", crop.sunNeed.name.lowercase().replaceFirstChar { it.uppercase() }))
    add(Fact("Water", crop.waterNeed.name.lowercase().replaceFirstChar { it.uppercase() }))
    add(Fact("Soil pH", "${crop.soilPhRange.start}–${crop.soilPhRange.endInclusive}"))
    if (crop.daysSeedlingToHarvest.first > 0) {
        val r = crop.daysSeedlingToHarvest
        val txt = if (r.first == r.last) "${r.first} d" else "${r.first}–${r.last} d"
        add(Fact("To harvest", txt))
    }
    crop.yearsToFirstFruit?.let { add(Fact("Years→fruit", "$it")) }
    crop.potSizeLitres?.let { add(Fact("Pot size", "$it L")) }
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        color = OnSurfaceInk,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun PestRow(pest: Pest) {
    Column {
        Text(pest.name, color = OnSurfaceInk, style = MaterialTheme.typography.titleMedium)
        Text(
            pest.shortDescription,
            color = OnSurfaceInk.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DiseaseRow(disease: Disease) {
    Column {
        Text(disease.name, color = OnSurfaceInk, style = MaterialTheme.typography.titleMedium)
        Text(
            disease.description,
            color = OnSurfaceInk.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
