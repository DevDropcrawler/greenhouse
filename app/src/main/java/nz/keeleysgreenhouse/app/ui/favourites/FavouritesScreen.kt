package nz.keeleysgreenhouse.app.ui.favourites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    onBack: () -> Unit,
    onCropClick: (Int) -> Unit,
    onPestClick: (Int) -> Unit,
    onDiseaseClick: (Int) -> Unit,
    viewModel: FavouritesViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Favourites") },
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
        if (ui.isEmpty) {
            EmptyState(inner)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(inner),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (ui.crops.isNotEmpty()) {
                    item { SectionHeader("Crops") }
                    items(ui.crops, key = { "c${it.id}" }) { crop ->
                        CropFavRow(crop, onClick = { onCropClick(crop.id) })
                    }
                }
                if (ui.pests.isNotEmpty()) {
                    item { SectionHeader("Pests") }
                    items(ui.pests, key = { "p${it.id}" }) { pest ->
                        PestFavRow(pest, onClick = { onPestClick(pest.id) })
                    }
                }
                if (ui.diseases.isNotEmpty()) {
                    item { SectionHeader("Diseases") }
                    items(ui.diseases, key = { "d${it.id}" }) { disease ->
                        DiseaseFavRow(disease, onClick = { onDiseaseClick(disease.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(inner: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(inner).padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = OliveMoss,
                modifier = Modifier.size(64.dp)
            )
            Box(Modifier.height(12.dp))
            Text(
                text = "Tap the heart on a crop, pest, or disease to save it here.",
                color = OnSurfaceInk.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        color = OliveMoss,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun CropFavRow(crop: Crop, onClick: () -> Unit) {
    val ctx = LocalContext.current
    val imgId = remember(crop.imageResName) {
        if (crop.imageResName.isBlank()) 0
        else ctx.resources.getIdentifier(crop.imageResName, "drawable", ctx.packageName)
    }
    FavRow(
        title = crop.commonName,
        subtitle = crop.family,
        imageResId = imgId,
        fallbackLetter = crop.commonName.take(1).uppercase(),
        onClick = onClick
    )
}

@Composable
private fun PestFavRow(pest: Pest, onClick: () -> Unit) {
    val ctx = LocalContext.current
    val imgId = remember(pest.pestImageResName) {
        if (pest.pestImageResName.isBlank()) 0
        else ctx.resources.getIdentifier(pest.pestImageResName, "drawable", ctx.packageName)
    }
    FavRow(
        title = pest.name,
        subtitle = pest.shortDescription,
        imageResId = imgId,
        fallbackLetter = pest.name.take(1).uppercase(),
        onClick = onClick
    )
}

@Composable
private fun DiseaseFavRow(disease: Disease, onClick: () -> Unit) {
    val ctx = LocalContext.current
    val imgId = remember(disease.imageResName) {
        if (disease.imageResName.isBlank()) 0
        else ctx.resources.getIdentifier(disease.imageResName, "drawable", ctx.packageName)
    }
    FavRow(
        title = disease.name,
        subtitle = disease.description,
        imageResId = imgId,
        fallbackLetter = disease.name.take(1).uppercase(),
        onClick = onClick
    )
}

@Composable
private fun FavRow(
    title: String,
    subtitle: String,
    imageResId: Int,
    fallbackLetter: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(imageResId),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brass.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fallbackLetter,
                        color = OliveMoss,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    color = OnSurfaceInk,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = OnSurfaceInk.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
