package nz.keeleysgreenhouse.app.ui.crops

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.CropCategory
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk

private data class CategoryChip(val category: CropCategory?, val label: String)

private val Chips = listOf(
    CategoryChip(null, "All"),
    CategoryChip(CropCategory.FRUITING, "Fruiting"),
    CategoryChip(CropCategory.LEAFY, "Leafy"),
    CategoryChip(CropCategory.HERB, "Herbs"),
    CategoryChip(CropCategory.BRASSICA, "Brassica"),
    CategoryChip(CropCategory.ROOT, "Root"),
    CategoryChip(CropCategory.LEGUME, "Legume"),
    CategoryChip(CropCategory.ALLIUM, "Allium"),
    CategoryChip(CropCategory.CUCURBIT, "Cucurbit"),
    CategoryChip(CropCategory.FRUIT_TREE, "Fruit trees"),
    CategoryChip(CropCategory.VINE_BERRY, "Vine & berry"),
    CategoryChip(CropCategory.PERENNIAL, "Perennial"),
    CategoryChip(CropCategory.MICROGREEN, "Microgreen"),
)

@Composable
fun CropsScreen(
    contentPadding: PaddingValues,
    onCropClick: (Int) -> Unit,
    viewModel: CropsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .padding(contentPadding)
    ) {
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(Chips, key = { it.label }) { chip ->
                FilterChip(
                    label = chip.label,
                    selected = state.selectedCategory == chip.category,
                    onClick = { viewModel.selectCategory(chip.category) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.crops, key = { it.id }) { crop ->
                CropGridCard(crop = crop, onClick = { onCropClick(crop.id) })
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Forest else Color.Transparent
    val fg = if (selected) Cream else OnSurfaceInk
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CropGridCard(crop: Crop, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            val ctx = LocalContext.current
            val imgId = remember(crop.imageResName) {
                ctx.resources.getIdentifier(crop.imageResName, "drawable", ctx.packageName)
            }
            if (imgId != 0) {
                Image(
                    painter = painterResource(imgId),
                    contentDescription = crop.commonName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Brass.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = crop.commonName.take(1).uppercase(),
                        color = Forest,
                        style = MaterialTheme.typography.displayMedium
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
