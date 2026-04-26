package nz.keeleysgreenhouse.app.ui.pests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.domain.usecase.PestDetail
import nz.keeleysgreenhouse.app.ui.components.AccordionSection
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestDetailScreen(
    onBack: () -> Unit,
    onCropClick: (Int) -> Unit,
    viewModel: PestDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.detail?.pest?.name ?: "Pest") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
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
            ) { Text("Pest not found", color = OnSurfaceInk) }

            else -> PestDetailContent(
                detail = state.detail!!,
                contentPadding = inner,
                onCropClick = onCropClick
            )
        }
    }
}

@Composable
private fun PestDetailContent(
    detail: PestDetail,
    contentPadding: PaddingValues,
    onCropClick: (Int) -> Unit
) {
    val pest = detail.pest
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Brass.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pest.name.take(1).uppercase(),
                color = OliveMoss,
                style = MaterialTheme.typography.displayLarge
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            Text(
                text = pest.name,
                color = OliveMoss,
                style = MaterialTheme.typography.displayMedium
            )
            if (pest.aliases.isNotEmpty()) {
                Text(
                    text = "Also known as: " + pest.aliases.joinToString(),
                    color = OnSurfaceInk.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(Modifier.height(12.dp))
            BodyText(pest.shortDescription)
            Spacer(Modifier.height(8.dp))

            AccordionSection(title = "Symptoms", initiallyExpanded = true) {
                BodyText(pest.symptoms)
            }
            if (pest.organicControls.isNotEmpty()) {
                AccordionSection(title = "Organic controls") {
                    BulletList(pest.organicControls)
                }
            }
            if (pest.biologicalControls.isNotEmpty()) {
                AccordionSection(title = "Biological controls") {
                    BulletList(pest.biologicalControls)
                }
            }
            if (pest.chemicalNotes.isNotBlank()) {
                AccordionSection(title = "Chemical notes") {
                    BodyText(pest.chemicalNotes)
                }
            }
            if (pest.preventionTips.isNotEmpty()) {
                AccordionSection(title = "Prevention") {
                    BulletList(pest.preventionTips)
                }
            }
            if (detail.affectedCrops.isNotEmpty()) {
                AccordionSection(title = "Affected crops", initiallyExpanded = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        detail.affectedCrops.forEach { crop ->
                            AffectedCropRow(crop = crop, onClick = { onCropClick(crop.id) })
                        }
                    }
                }
            }
            if (pest.youtubeVideoIds.isNotEmpty() || pest.youtubeSearchQuery.isNotBlank()) {
                AccordionSection(title = "Videos") {
                    BodyText(
                        if (pest.youtubeVideoIds.isNotEmpty())
                            "${pest.youtubeVideoIds.size} curated video(s) — playback in Phase 8."
                        else "Search: ${pest.youtubeSearchQuery}"
                    )
                }
            }
        }
    }
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
private fun BulletList(items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.forEach { item ->
            Text(
                text = "•  $item",
                color = OnSurfaceInk,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AffectedCropRow(crop: Crop, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Text(
            text = crop.commonName,
            color = OliveMoss,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = crop.family,
            color = OnSurfaceInk.copy(alpha = 0.55f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
