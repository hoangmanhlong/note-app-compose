package com.example.notes.data

import com.example.notes.data.local.NoteDao
import com.example.notes.data.local.Note
import com.example.notes.data.local.SearchHistory
import com.example.notes.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(private val dao: NoteDao) : NoteRepository {
    override fun getNotes(): Flow<List<Note>> = dao.getNotes()
    override suspend fun getNote(id: Int): Note? = dao.getNote(id)
    override suspend fun insertNote(note: Note) = dao.insertNote(note)
    override suspend fun deleteNote(id: Int) = dao.deleteNote(id)
    override suspend fun searchByTitle(query: String): List<Note>? = dao.searchByTitle(query)
    override suspend fun insertSearchHistory(searchHistory: SearchHistory) = dao.insertSearchHistory(searchHistory)
    override suspend fun deleteSearchHistory(searchHistory: SearchHistory) = dao.deleteSearchHistory(searchHistory)
    override fun getSearchHistories(): Flow<List<SearchHistory>> = dao.getSearchHistories()
}