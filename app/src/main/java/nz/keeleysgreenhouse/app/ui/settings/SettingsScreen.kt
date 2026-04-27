package nz.keeleysgreenhouse.app.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.ui.components.EyebrowLabel
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.AppError
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var timePickerOpen by remember { mutableStateOf(false) }
    var resetDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader("Notifications")
            SettingRow(
                title = "Daily reminder time",
                value = formatTime(state.reminderHour, state.reminderMinute),
                onClick = { timePickerOpen = true }
            )

            SectionHeader("Data")
            SettingRow(
                title = "Reset all data",
                value = "Re-seed crops, pests, diseases",
                valueColor = AppError,
                onClick = { resetDialogOpen = true }
            )
            SettingRow(
                title = "Data version",
                value = "v1.0 · 2026-04",
                onClick = null
            )

            SectionHeader("About")
            AboutBlock()

            Spacer(Modifier.height(32.dp))
        }
    }

    if (timePickerOpen) {
        val pickerState = rememberTimePickerState(
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { timePickerOpen = false },
            title = { Text("Daily reminder time") },
            text = { TimePicker(state = pickerState) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setReminderTime(pickerState.hour, pickerState.minute)
                    timePickerOpen = false
                }) { Text("Save", color = Forest) }
            },
            dismissButton = {
                TextButton(onClick = { timePickerOpen = false }) {
                    Text("Cancel", color = OnSurfaceInk)
                }
            },
            containerColor = Cream
        )
    }

    if (resetDialogOpen) {
        AlertDialog(
            onDismissRequest = { resetDialogOpen = false },
            title = { Text("Reset all data?") },
            text = {
                Text("This clears your plantings, tasks, and search history, then re-seeds the bundled crops, pests and diseases.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetData()
                    resetDialogOpen = false
                }) { Text("Reset", color = AppError) }
            },
            dismissButton = {
                TextButton(onClick = { resetDialogOpen = false }) {
                    Text("Cancel", color = OnSurfaceInk)
                }
            },
            containerColor = Cream
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Box(modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)) {
        EyebrowLabel(text = text)
    }
}

@Composable
private fun SettingRow(
    title: String,
    value: String,
    valueColor: Color = OnSurfaceInk.copy(alpha = 0.7f),
    onClick: (() -> Unit)?
) {
    val mod = Modifier
        .fillMaxWidth()
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(horizontal = 24.dp, vertical = 14.dp)
    Row(
        modifier = mod,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = OnSurfaceInk, style = MaterialTheme.typography.titleMedium)
        Text(value, color = valueColor, style = MaterialTheme.typography.bodyMedium)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(1.dp)
            .background(Brass.copy(alpha = 0.4f))
    )
}

@Composable
private fun AboutBlock() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(
            "Keeley's Greenhouse",
            color = OliveMoss,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Your Papamoa greenhouse, month by month.",
            color = OnSurfaceInk,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Offline-first sowing, transplant and harvest planner tuned for the warm Bay of Plenty greenhouse zone. No accounts, no cloud, no analytics.",
            color = OnSurfaceInk,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Climate data: NIWA Tauranga / Bay of Plenty. Sowing windows: Tui Garden, Daltons, Kings Plant Barn, Edible Backyard. Pest controls: Bioforce NZ. Photos: contributing growers and Wikimedia (CC-BY).",
            color = OnSurfaceInk.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String =
    "%02d:%02d".format(hour, minute)
