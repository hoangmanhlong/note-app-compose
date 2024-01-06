package com.example.notes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val string: String,
    val time: Long = System.currentTimeMillis()
)