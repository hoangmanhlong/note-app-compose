package com.example.notes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class, SearchHistory::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract val dao: NoteDao
    companion object {
        const val DATABASE_NAME = "note_database"
    }
}