package com.example.notes.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.local.Note
import com.example.notes.data.local.SearchHistory
import com.example.notes.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: NoteRepository) : ViewModel() {

    val uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
//        repository.getSearchHistories().map {
//            SearchUiState(histories = it)
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000L),
//            initialValue = SearchUiState()
//        )

    init {
        repository.getSearchHistories()
            .map { SearchUiState(histories = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = SearchUiState()
            )
            .onEach { uiState.value = it }
            .launchIn(viewModelScope)
    }

    suspend fun search(query: String) {
        uiState.value.result = repository.searchByTitle(query) ?: emptyList()
//        uiState.value.copy(result = repository.searchByTitle(query) ?: emptyList())
    }

    fun updateQuery(query: String) {
        uiState.value = uiState.value.copy(query = query)
        Log.d(this.toString(), "updateQuery: $query")
    }

    suspend fun addSearchHistory(string: String) {
        repository.insertSearchHistory(SearchHistory(string))
    }

    suspend fun deleteSearchHistory(searchHistory: SearchHistory) {
        repository.deleteSearchHistory(searchHistory)
    }

    fun clearResult() {
        uiState.value.result = emptyList()
    }
}

data class SearchUiState(
    var result: List<Note> = emptyList(),
    var query: String = "",
    val histories: List<SearchHistory> = emptyList()
)