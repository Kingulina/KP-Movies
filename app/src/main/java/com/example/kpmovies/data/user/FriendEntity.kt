package com.example.kpmovies.data.user

import androidx.room.Entity

@Entity(
    tableName = "friends",
    primaryKeys = ["owner", "followee"]      // kompozytowy PK
)
data class FriendEntity(
    val owner: String,       // login zalogowanego uż.
    val followee: String     // login obserwowanego
)