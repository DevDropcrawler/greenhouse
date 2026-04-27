package nz.keeleysgreenhouse.app.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.ui.components.EyebrowLabel
import nz.keeleysgreenhouse.app.ui.theme.AppError
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantingDetailScreen(
    onBack: () -> Unit,
    viewModel: PlantingDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var confirmDelete by remember { mutableStateOf(false) }

    val planting = state.planting
    val crop = state.crop

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = crop?.commonName ?: "",
                        color = Cream,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Cream
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OliveMoss)
            )
        },
        containerColor = Cream
    ) { padding ->
        if (planting == null || crop == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding))
            return@Scaffold
        }

        val row = remember(planting, crop) {
            GardenViewModel.toRow(planting, crop, LocalDate.now())
        }
        val fmt = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EyebrowLabel(text = planting.location)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { row.progress.coerceIn(0f, 1f) },
                        color = Forest,
                        trackColor = Forest.copy(alpha = 0.18f),
                        modifier = Modifier.fillMaxWidth().height(10.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    DetailRow(stringResource(R.string.planting_sown), LocalDate.parse(planting.sownDate).format(fmt))
                    DetailRow(
                        stringResource(R.string.planting_transplant),
                        planting.transplantedDate?.let { LocalDate.parse(it).format(fmt) } ?: "—"
                    )
                    val hStart = LocalDate.parse(planting.expectedHarvestStart).format(fmt)
                    val hEnd = LocalDate.parse(planting.expectedHarvestEnd).format(fmt)
                    DetailRow(stringResource(R.string.planting_harvest_window), "$hStart → $hEnd")
                }
            }

            if (planting.notes.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EyebrowLabel(text = stringResource(R.string.planting_notes))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = planting.notes,
                            color = OnSurfaceInk,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            if (planting.transplantedDate == null) {
                Button(
                    onClick = { viewModel.markTransplanted() },
                    colors = ButtonDefaults.buttonColors(containerColor = Forest, contentColor = Cream),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(stringResource(R.string.planting_mark_transplanted))
                }
            }

            OutlinedButton(
                onClick = { confirmDelete = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppError),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(stringResource(R.string.planting_delete))
            }
        }

        if (confirmDelete) {
            AlertDialog(
                onDismissRequest = { confirmDelete = false },
                title = { Text(stringResource(R.string.planting_delete_confirm_title)) },
                text = { Text(stringResource(R.string.planting_delete_confirm_body)) },
                confirmButton = {
                    TextButton(onClick = {
                        confirmDelete = false
                        viewModel.delete(onBack)
                    }) { Text(stringResource(R.string.planting_delete_confirm_yes), color = AppError) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDelete = false }) {
                        Text(stringResource(R.string.planting_delete_confirm_no))
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, color = Brass, style = MaterialTheme.typography.labelMedium)
        Text(text = value, color = OnSurfaceInk, style = MaterialTheme.typography.titleMedium)
    }
}
