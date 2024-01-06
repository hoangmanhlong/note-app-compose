package com.example.notes.ui.add_edit

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notes.R
import com.example.notes.data.local.Note
import com.example.notes.ui.NoteAppBar
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: AddEditViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val uiState = viewModel.uiState
    Scaffold(
        topBar = {
            NoteAppBar(
                label = "",
                canNavigateBack = true,
                action = {
                    if(viewModel.validateInput(uiState.note))
                        IconButton(onClick = {
                            coroutineScope.launch {
                                viewModel.saveNote()
                                navigateUp()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                },
                scrollBehavior = scrollBehavior,
                navigateUp = navigateUp
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        NoteInputForm(
            addEditUiState = uiState,
            onValueChange = viewModel::updateNote,
            modifier = modifier.padding(it)
        )
    }
}

@Composable
fun NoteInputForm(
    addEditUiState: AddEditUiState,
    onValueChange: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val note = addEditUiState.note
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ContentTextField(
            label = R.string.title,
            value = note.title,
            onValueChange = { onValueChange(note.copy(title = it)) },

            onKeyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            isHintVisible = note.title.isBlank(),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
        )

        ContentTextField(
            label = R.string.content,
            onValueChange = { onValueChange(note.copy(content = it)) },
            value = note.content,
            onKeyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Default
            ),
            isHintVisible = note.content.isBlank(),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ContentTextField(
    @StringRes label: Int,
    onValueChange: (String) -> Unit,
    value: String,
    onKeyboardOptions: KeyboardOptions,
    isHintVisible: Boolean = true,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        BasicTextField(
            onValueChange = onValueChange,
            value = value,
            keyboardOptions = onKeyboardOptions,
            textStyle = textStyle,
            modifier = modifier
        )
        if (isHintVisible)
            Text(stringResource(label), color = Color.Gray)
    }
}