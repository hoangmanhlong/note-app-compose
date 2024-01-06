package com.example.notes.ui.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notes.R
import com.example.notes.data.local.SearchHistory
import com.example.notes.ui.home.NotesList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {},
    onNoteClick: (Int) -> Unit
) {
    SearchBarBody(
        viewModel = viewModel,
        onNavigateUp = onNavigateUp,
        modifier = Modifier.fillMaxWidth(),
        onNoteClick = onNoteClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarBody(
    viewModel: SearchViewModel,
    onNavigateUp: () -> Unit = {},
    onNoteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    SearchBar(
        query = uiState.query,
        onQueryChange = {
            coroutineScope.launch { viewModel.search(it) }
            viewModel.updateQuery(it)
//            if (uiState.query != it) viewModel.clearResult()
        },
        onSearch = {
            coroutineScope.launch {
                viewModel.search(it)
                if (it.isNotBlank()) viewModel.addSearchHistory(it)
            }
            viewModel.updateQuery(it)
            if (uiState.query != it) viewModel.clearResult()
        },
        active = true,
        onActiveChange = {},
        placeholder = { Text(stringResource(id = R.string.search)) },
        leadingIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
        },
        trailingIcon = {
            if (uiState.query.isNotBlank())
                IconButton(onClick = { viewModel.updateQuery("") }) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Clear")
                }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        if (uiState.query.isBlank())
            ListHistory(
                viewModel = viewModel,
                histories = uiState.histories,
                modifier = Modifier.fillMaxWidth()
            )
        else {
            SearchBody(uiState = uiState, onNoteClick = onNoteClick)
        }
    }
}

@Composable
fun SearchBody(uiState: SearchUiState, onNoteClick: (Int) -> Unit) {
    if (uiState.result.isNotEmpty()) {
        NotesList(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = dimensionResource(R.dimen.list_and_detail_list_padding_vertical),
                    horizontal = dimensionResource(R.dimen.list_and_detail_list_padding_top)
                ),
            onCardClick = onNoteClick,
            notes = uiState.result
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.search_empty))
        }
    }
}

@Composable
fun ListHistory(
    viewModel: SearchViewModel,
    histories: List<SearchHistory>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope();
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(histories) {
            HistoryCard(
                onDelete = {
                    coroutineScope.launch {
                        viewModel.deleteSearchHistory(it)
                    }
                },
                text = it.string,
                modifier = Modifier.fillMaxWidth().clickable {
                    viewModel.updateQuery(it.string)
                    coroutineScope.launch {
                        viewModel.search(it.string)
                    }
                }
            )
        }
    }
}

@Composable
fun HistoryCard(
    onDelete: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(imageVector = Icons.Default.History, contentDescription = null)
            Text(text, modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
            }
        }
    }
}