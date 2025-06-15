
package com.example.kpmovies.data.local.dao

import androidx.room.*
import com.example.kpmovies.data.local.entity.WatchlistEntity

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE owner=:u AND movieId=:m")
    suspend fun remove(u: String, m: String)

    @Query("SELECT * FROM watchlist WHERE owner=:u")
    suspend fun all(u: String): List<WatchlistEntity>
}