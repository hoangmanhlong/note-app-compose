package com.example.notes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM note ORDER BY time DESC")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNote(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteNote(id: Int)

    @Query(" SELECT * FROM note WHERE title LIKE  '%' || :query || '%'")
    suspend fun searchByTitle(query: String): List<Note>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    @Delete
    suspend fun deleteSearchHistory(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history ORDER BY time DESC")
    fun getSearchHistories(): Flow<List<SearchHistory>>
}