package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creation_items")
data class CreationItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val contentType: String, // TEXT, IMAGE, THUMBNAIL, FOREX, TRANSLATION
    val inputPrompt: String,
    val outputContent: String,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
