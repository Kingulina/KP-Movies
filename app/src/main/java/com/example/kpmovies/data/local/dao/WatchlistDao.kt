package com.example.kpmovies.data.local.dao

import androidx.room.*
import com.example.kpmovies.data.local.entity.WatchlistEntity

@Dao
interface WatchlistDao {
    /** pobierz listę filmów użytkownika */
    @Query("""
        SELECT *
        FROM watchlist
        WHERE owner = :owner
    """)
    suspend fun all(owner: String): List<WatchlistEntity>

    /** dodaj film do watch-listy */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(item: WatchlistEntity)

    /** usuń film z watch-listy */
    @Query("""
        DELETE FROM watchlist
        WHERE owner   = :owner
          AND movieId = :movieId
    """)
    suspend fun remove(owner: String, movieId: String)
}
