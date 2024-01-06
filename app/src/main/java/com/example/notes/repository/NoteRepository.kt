package com.example.notes.repository

import com.example.notes.data.local.Note
import com.example.notes.data.local.SearchHistory
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    suspend fun getNote(id: Int): Note?
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(id: Int)
    suspend fun searchByTitle(query: String): List<Note>?
    suspend fun insertSearchHistory(searchHistory: SearchHistory)
    suspend fun deleteSearchHistory(searchHistory: SearchHistory)
    fun getSearchHistories(): Flow<List<SearchHistory>>
}