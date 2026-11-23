package com.example.mynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis(),
    val completed: Boolean = false,
    val imageUri: String? = null
)
