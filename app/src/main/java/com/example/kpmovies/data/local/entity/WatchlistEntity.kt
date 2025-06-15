package com.example.kpmovies.data.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["owner","movieId"])
data class WatchlistEntity(
    val owner: String,
    val movieId: String,
    val status: String          // "TODO" | "WATCHED"
)