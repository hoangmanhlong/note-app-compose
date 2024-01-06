package com.example.notes.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notes.R
import com.example.notes.data.local.Note
import com.example.notes.ui.NoteAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCreateNewNote: () -> Unit = {},
    onNoteClick: (Int) -> Unit = {},
    onSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val homeUiState by viewModel.homeUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            NoteAppBar(
                label = stringResource(id = R.string.app_name),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                action = {
                    IconButton(onClick = onSearch) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNewNote,
                modifier = Modifier.padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null
                )
            }
        }
    ) {
        BodyContent(
            onProcess = { id -> viewModel.updateNoteIdClicked(id) },
            homeUiState = homeUiState,
            onNoteClick = onNoteClick,
            onLongClick = {noteId ->
                showBottomSheet = true
                viewModel.updateNoteIdClicked(noteId)
            },
            modifier = modifier
                .padding(it)
                .fillMaxSize(),
            showBottomSheet = showBottomSheet
        )
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = {
                        coroutineScope.launch { viewModel.deleteNote(homeUiState.noteIdClick) }
                        showBottomSheet = false
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                    }
                    Text(text = stringResource(id = R.string.delete))
                }
            }
        }
    }
}

@Composable
fun BodyContent(
    showBottomSheet: Boolean,
    onProcess: (Int) -> Unit = {},
    homeUiState: HomeUiState,
    onNoteClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (homeUiState.notes.isNotEmpty()) {
            NotesList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        vertical = dimensionResource(R.dimen.list_and_detail_list_padding_vertical),
                        horizontal = dimensionResource(R.dimen.list_and_detail_list_padding_top)
                    ),
                onCardClick = onNoteClick,
                notes = homeUiState.notes,
                onLongClick = onLongClick,
                onProcess = onProcess,
                showBottomSheet = showBottomSheet
            )

        } else {
            Image(
                painter = painterResource(R.drawable.empty_list),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesList(
    showBottomSheet: Boolean = false,
    notes: List<Note>,
    onProcess: (Int) -> Unit = {},
    onCardClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.note_list_item_vertical_spacing)
        ),
        modifier = modifier
    ) {
        items(notes, key = { note -> note.id }) { note ->
            NoteCard(
                note = note,
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            onCardClick(note.id)
                            onProcess(note.id)
                        },
                        onLongClick = { onLongClick(note.id) }
                    )
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors()
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            Text(
                text = getFormattedTime(note.time),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

    }
}
