package com.example.kpmovies.data.local.dao

import androidx.room.*
import com.example.kpmovies.data.local.entity.ReviewEntity

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(r: ReviewEntity)

    @Query("SELECT * FROM review WHERE movieId=:m ORDER BY created DESC LIMIT 20")
    suspend fun latest(m: String): List<ReviewEntity>

    @Query("SELECT AVG(rating) FROM review WHERE movieId=:m")
    suspend fun avg(m: String): Double?

    @Query("SELECT * FROM review WHERE author IN (:authors) ORDER BY created DESC LIMIT 2")
    suspend fun latestFromUsers(authors: List<String>): List<ReviewEntity>
}