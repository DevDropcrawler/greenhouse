package nz.keeleysgreenhouse.app.ui.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nz.keeleysgreenhouse.app.R
import nz.keeleysgreenhouse.app.data.entity.Crop
import nz.keeleysgreenhouse.app.data.entity.Disease
import nz.keeleysgreenhouse.app.data.entity.Pest
import nz.keeleysgreenhouse.app.data.entity.SearchHistory
import nz.keeleysgreenhouse.app.ui.components.EyebrowLabel
import nz.keeleysgreenhouse.app.ui.theme.Brass
import nz.keeleysgreenhouse.app.ui.theme.Cream
import nz.keeleysgreenhouse.app.ui.theme.Forest
import nz.keeleysgreenhouse.app.ui.theme.OliveMoss
import nz.keeleysgreenhouse.app.ui.theme.OnSurfaceInk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onCropClick: (Int) -> Unit,
    onPestClick: (Int) -> Unit,
    onDiseaseClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.search_title)) },
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                singleLine = true,
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = viewModel::onClearQuery) {
                            Icon(Icons.Outlined.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OliveMoss,
                    unfocusedBorderColor = Brass.copy(alpha = 0.5f),
                    cursorColor = OliveMoss
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { viewModel.onSubmit() }
                )
            )
            Spacer(Modifier.height(16.dp))

            if (state.query.trim().isEmpty()) {
                RecentSearchesPane(
                    recent = state.recent,
                    onItemClick = viewModel::onRecentClick,
                    onItemRemove = viewModel::onRecentRemove,
                    onClearAll = viewModel::onClearAllRecent
                )
            } else {
                ResultsPane(
                    state = state,
                    onCropClick = onCropClick,
                    onPestClick = onPestClick,
                    onDiseaseClick = onDiseaseClick
                )
            }
        }
    }
}

@Composable
private fun RecentSearchesPane(
    recent: List<SearchHistory>,
    onItemClick: (String) -> Unit,
    onItemRemove: (String) -> Unit,
    onClearAll: () -> Unit
) {
    if (recent.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EyebrowLabel(stringResource(R.string.search_recent), modifier = Modifier.weight(1f))
        TextButton(onClick = onClearAll) {
            Text(stringResource(R.string.search_clear_all), color = OliveMoss)
        }
    }
    Spacer(Modifier.height(4.dp))
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(recent, key = { it.id }) { entry ->
            RecentRow(
                query = entry.query,
                onClick = { onItemClick(entry.query) },
                onRemove = { onItemRemove(entry.query) }
            )
        }
    }
}

@Composable
private fun RecentRow(query: String, onClick: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            tint = OnSurfaceInk.copy(alpha = 0.55f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = query,
            color = OnSurfaceInk,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = stringResource(R.string.search_remove),
                tint = OnSurfaceInk.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun ResultsPane(
    state: SearchUiState,
    onCropClick: (Int) -> Unit,
    onPestClick: (Int) -> Unit,
    onDiseaseClick: (Int) -> Unit
) {
    if (!state.isLoading && state.results.isEmpty) {
        Text(
            text = stringResource(R.string.search_no_results),
            color = OnSurfaceInk.copy(alpha = 0.55f),
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (state.results.crops.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.search_section_crops)) }
            items(state.results.crops, key = { "c-${it.id}" }) { crop ->
                ResultCard(
                    title = crop.commonName,
                    subtitle = crop.family,
                    initial = crop.commonName,
                    onClick = { onCropClick(crop.id) }
                )
            }
        }
        if (state.results.pests.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.search_section_pests)) }
            items(state.results.pests, key = { "p-${it.id}" }) { pest ->
                ResultCard(
                    title = pest.name,
                    subtitle = pest.shortDescription,
                    initial = pest.name,
                    onClick = { onPestClick(pest.id) }
                )
            }
        }
        if (state.results.diseases.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.search_section_diseases)) }
            items(state.results.diseases, key = { "d-${it.id}" }) { disease ->
                ResultCard(
                    title = disease.name,
                    subtitle = disease.description,
                    initial = disease.name,
                    onClick = { onDiseaseClick(disease.id) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column {
        Spacer(Modifier.height(4.dp))
        EyebrowLabel(title)
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ResultCard(
    title: String,
    subtitle: String,
    initial: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brass.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial.take(1).uppercase(),
                    color = Forest,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = OnSurfaceInk,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        color = OnSurfaceInk.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
