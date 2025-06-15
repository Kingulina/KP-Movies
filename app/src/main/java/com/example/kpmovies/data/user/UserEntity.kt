package com.example.kpmovies.data.user

import androidx.room.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val login: String,
    val password: String            // demo: zwykły tekst; w produkcji haszuj!
)