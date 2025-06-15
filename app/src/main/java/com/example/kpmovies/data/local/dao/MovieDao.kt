package com.example.kpmovies.data.local.dao

import androidx.room.*
import com.example.kpmovies.data.local.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :q || '%'")
    suspend fun searchLocal(q: String): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id=:id")
    suspend fun one(id: String): MovieEntity?
}