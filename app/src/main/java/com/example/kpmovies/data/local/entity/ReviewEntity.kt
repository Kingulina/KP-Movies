package com.example.kpmovies.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["movieId","author"])
data class ReviewEntity(
    val movieId: String,
    val author: String,
    val rating: Int,
    val text: String,
    val created: Long = System.currentTimeMillis()
)