package nz.keeleysgreenhouse.app.ui.garden

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.ui.components.EyebrowLabel
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantingScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddPlantingViewModel = hiltViewModel()
) {
    val form by viewModel.form.collectAsState()
    val crops by viewModel.crops.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.saved.collect { onSaved() }
    }

    var pickingCrop by remember { mutableStateOf(false) }
    var pickingDate by remember { mutableStateOf(false) }
    val pickedCrop = crops.firstOrNull { it.id == form.cropId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.add_planting_title),
                        color = Cream,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.add_planting_cancel),
                            tint = Cream
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OliveMoss)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Cream)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { viewModel.save() },
                    enabled = form.canSave,
                    colors = ButtonDefaults.buttonColors(containerColor = Forest, contentColor = Cream),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(stringResource(R.string.add_planting_save))
                }
            }
        },
        containerColor = Cream
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StepCard(title = stringResource(R.string.add_planting_step_crop)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pickingCrop = true }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = pickedCrop?.commonName ?: stringResource(R.string.add_planting_pick_crop),
                            color = if (pickedCrop != null) OnSurfaceInk else OnSurfaceInk.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            item {
                StepCard(title = stringResource(R.string.add_planting_step_date)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pickingDate = true }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = form.sownDate.format(
                                DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())
                            ),
                            color = OnSurfaceInk,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            item {
                StepCard(title = stringResource(R.string.add_planting_step_location)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Greenhouse" to stringResource(R.string.add_planting_location_greenhouse),
                            "Outdoor" to stringResource(R.string.add_planting_location_outdoor)
                        ).forEach { (value, label) ->
                            FilterChip(
                                selected = form.location == value,
                                onClick = { viewModel.update { it.copy(location = value) } },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Forest,
                                    selectedLabelColor = Cream
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = form.bay,
                        onValueChange = { v -> viewModel.update { it.copy(bay = v) } },
                        label = { Text(stringResource(R.string.add_planting_bay_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                StepCard(title = stringResource(R.string.add_planting_step_notes)) {
                    OutlinedTextField(
                        value = form.notes,
                        onValueChange = { v -> viewModel.update { it.copy(notes = v) } },
                        placeholder = { Text(stringResource(R.string.add_planting_notes_hint)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }
            }
            if (pickedCrop != null) {
                item {
                    ReviewCard(form = form, cropName = pickedCrop.commonName)
                }
            }
        }
    }

    if (pickingCrop) {
        CropPickerDialog(
            crops = crops,
            onPick = { id ->
                viewModel.update { it.copy(cropId = id) }
                pickingCrop = false
            },
            onDismiss = { pickingCrop = false }
        )
    }

    if (pickingDate) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = form.sownDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { pickingDate = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { ms ->
                        val date = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.update { it.copy(sownDate = date) }
                    }
                    pickingDate = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { pickingDate = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun StepCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            EyebrowLabel(text = title)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ReviewCard(form: AddPlantingFormState, cropName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = OliveMoss),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            EyebrowLabel(text = "Review")
            Spacer(Modifier.height(8.dp))
            Text(
                text = cropName,
                color = Cream,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Sow ${form.sownDate.format(DateTimeFormatter.ofPattern("d MMM"))} · ${form.location}" +
                    if (form.bay.isNotBlank()) " · ${form.bay}" else "",
                color = Brass,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CropPickerDialog(
    crops: List<nz.keeleysgreenhouse.app.data.entity.Crop>,
    onPick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Cream),
            modifier = Modifier.fillMaxWidth().height(560.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.add_planting_step_crop),
                    color = OliveMoss,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(crops, key = { it.id }) { crop ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPick(crop.id) }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    text = crop.commonName,
                                    color = OnSurfaceInk,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = crop.family,
                                    color = OnSurfaceInk.copy(alpha = 0.55f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
