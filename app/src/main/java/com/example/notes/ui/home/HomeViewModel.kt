package com.example.notes.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.local.Note
import com.example.notes.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    val homeUiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    init {
        repository.getNotes().map { notesEntity -> HomeUiState(notes = notesEntity) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = HomeUiState()
            )
            .onEach { homeUiState.value = it }
            .launchIn(viewModelScope)
    }

    suspend fun deleteNote(id: Int) {
        repository.deleteNote(id)
    }

    fun updateNoteIdClicked(id: Int) {
        homeUiState.value = homeUiState.value.copy(noteIdClick = id)
    }
}

data class HomeUiState(
    var noteIdClick: Int = -2,
    val notes: List<Note> = emptyList()
)

fun getFormattedTime(time: Long): String =
    SimpleDateFormat("h:mm a - dd/MM/yyyy", Locale.getDefault()).format(time)