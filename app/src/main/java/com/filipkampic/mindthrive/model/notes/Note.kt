package com.filipkampic.mindthrive.model.notes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val folderId: Int? = null,
    val title: String,
    val content: String,
    val timestamp: Long
)