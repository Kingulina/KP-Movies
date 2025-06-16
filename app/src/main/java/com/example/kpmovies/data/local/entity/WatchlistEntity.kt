package com.example.kpmovies.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "watchlist",
    primaryKeys = ["owner", "movieId"]
)
data class WatchlistEntity(
    val owner:   String,     // login
    val movieId: String      // imdbID
)
