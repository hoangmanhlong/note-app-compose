package com.example.notes.ui.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.local.Note
import com.example.notes.repository.NoteRepository
import com.example.notes.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[Screen.AddEditScreen.argumentKey])

    var uiState by mutableStateOf(AddEditUiState())
        private set

    init {
        if (itemId == -1) {
            uiState = AddEditUiState()
        } else {
            viewModelScope.launch {
                repository.getNote(itemId)?.let {
                    uiState.note = it
                }
            }
        }
    }

    fun updateNote(note: Note) {
        uiState = AddEditUiState(note = note, isvalidate = validateInput(note))
    }

    fun validateInput(note: Note): Boolean =
        with(note) { title.isNotBlank() || content.isNotBlank() }

    suspend fun saveNote() {
        repository.insertNote(note = uiState.note.copy(time = System.currentTimeMillis()))
    }
}

data class AddEditUiState(
    var note: Note = Note(title = "", content = ""),
    var isvalidate: Boolean = true
)