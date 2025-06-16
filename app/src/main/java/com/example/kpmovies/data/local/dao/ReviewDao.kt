package com.example.kpmovies.data.local.dao

import androidx.room.*
import com.example.kpmovies.data.local.entity.ReviewEntity

@Dao
interface ReviewDao {

    @Query("""
        SELECT *
        FROM review
        WHERE movieId = :movieId
        ORDER BY createdAt DESC
    """)
    suspend fun forMovie(movieId: String): List<ReviewEntity>

    @Query("""
        SELECT AVG(rating)
        FROM review
        WHERE movieId = :movieId
    """)
    suspend fun avg(movieId: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(review: ReviewEntity)

    @Query("""
        DELETE FROM review
        WHERE movieId = :movieId
          AND author  = :author
    """)
    suspend fun remove(movieId: String, author: String)
}