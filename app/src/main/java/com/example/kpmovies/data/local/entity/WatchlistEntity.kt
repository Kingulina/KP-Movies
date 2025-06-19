package com.example.kpmovies.data.local.entity

import androidx.room.Entity
import androidx.room.ColumnInfo


@Entity(
    tableName = "watchlist",
    primaryKeys = ["owner", "movieId"]
)
data class WatchlistEntity(
    val owner: String,        // login użytkownika
    val movieId: String,      // imdbID filmu
    @ColumnInfo(defaultValue = "'TODO'")
    val status: String,       // "TODO" lub "WATCHED
    @ColumnInfo(defaultValue = "0")
    val timestamp: Long = System.currentTimeMillis()
)