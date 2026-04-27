package nz.keeleysgreenhouse.app.ui.tasks

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.data.entity.Task
import nz.keeleysgreenhouse.app.ui.components.EyebrowLabel
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var tabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.tasks_title),
                        color = Cream,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Cream)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OliveMoss)
            )
        },
        containerColor = Cream
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Cream,
                contentColor = Forest
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = { Text(stringResource(R.string.tasks_tab_today)) }
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = { Text(stringResource(R.string.tasks_tab_all)) }
                )
            }

            val isAllTab = tabIndex == 1
            val groups = state.groups
            val todayTasks = state.today

            if (!isAllTab && todayTasks.isEmpty() && (groups.isEmpty() || groups.values.all { it.isEmpty() })) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.tasks_empty),
                        color = OnSurfaceInk.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                return@Column
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isAllTab) {
                    if (todayTasks.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.tasks_empty),
                                color = OnSurfaceInk.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(todayTasks, key = { it.id }) { task ->
                            TaskCard(task = task, onToggle = { viewModel.toggle(task) })
                        }
                    }
                } else {
                    val ordered = listOf(
                        TaskGroup.TODAY to R.string.tasks_group_today,
                        TaskGroup.TOMORROW to R.string.tasks_group_tomorrow,
                        TaskGroup.THIS_WEEK to R.string.tasks_group_week,
                        TaskGroup.LATER to R.string.tasks_group_later
                    )
                    ordered.forEach { (g, labelRes) ->
                        val list = groups[g].orEmpty()
                        if (list.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(8.dp))
                                EyebrowLabel(text = stringResource(labelRes))
                                Spacer(Modifier.height(4.dp))
                            }
                            items(list, key = { it.id }) { task ->
                                TaskCard(task = task, onToggle = { viewModel.toggle(task) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: Task, onToggle: () -> Unit) {
    val due = remember(task.dueDate) {
        LocalDate.parse(task.dueDate).format(DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()))
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (task.done) Forest else Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Brass),
                modifier = Modifier.size(22.dp)
            ) {
                if (task.done) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Cream,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = task.title,
                color = if (task.done) OnSurfaceInk.copy(alpha = 0.5f) else OnSurfaceInk,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = due,
                color = Brass,
                style = TextStyle(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
