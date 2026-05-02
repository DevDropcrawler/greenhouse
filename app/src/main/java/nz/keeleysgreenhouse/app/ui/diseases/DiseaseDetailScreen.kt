package nz.keeleysgreenhouse.app.ui.diseases

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.domain.usecase.DiseaseDetail
import nz.keeleysgreenhouse.app.ui.components.AccordionSection
import nz.keeleysgreenhouse.app.ui.components.VideoSection
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseDetailScreen(
    onBack: () -> Unit,
    onCropClick: (Int) -> Unit,
    viewModel: DiseaseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isFavourite by viewModel.isFavourite.collectAsState()
    val diseaseId = state.detail?.disease?.id

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.detail?.disease?.name ?: "Disease") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (diseaseId != null) {
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
            ) { Text("Disease not found", color = OnSurfaceInk) }

            else -> DiseaseDetailContent(
                detail = state.detail!!,
                contentPadding = inner,
                onCropClick = onCropClick
            )
        }
    }
}

@Composable
private fun DiseaseDetailContent(
    detail: DiseaseDetail,
    contentPadding: PaddingValues,
    onCropClick: (Int) -> Unit
) {
    val disease = detail.disease
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
                text = disease.name.take(1).uppercase(),
                color = OliveMoss,
                style = MaterialTheme.typography.displayLarge
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            Text(
                text = disease.name,
                color = OliveMoss,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(Modifier.height(12.dp))
            BodyText(disease.description)
            Spacer(Modifier.height(8.dp))

            AccordionSection(title = "Symptoms", initiallyExpanded = true) {
                BodyText(disease.symptoms)
            }
            if (disease.conditions.isNotBlank()) {
                AccordionSection(title = "Conditions") {
                    BodyText(disease.conditions)
                }
            }
            if (disease.controls.isNotEmpty()) {
                AccordionSection(title = "Controls") {
                    BulletList(disease.controls)
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
            if (disease.youtubeVideoIds.isNotEmpty() || disease.youtubeSearchQuery.isNotBlank()) {
                AccordionSection(title = "Videos", initiallyExpanded = true) {
                    VideoSection(
                        videoIds = disease.youtubeVideoIds,
                        searchQuery = disease.youtubeSearchQuery
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
