package com.example.kpmovies.data.local.dao

import androidx.room.*
import com.example.kpmovies.data.local.entity.WatchlistEntity

@Dao
interface WatchlistDao {

    /** wstaw lub nadpisz (zmienia status i timestamp) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: WatchlistEntity)

    /** usuń z dowolnego statusu */
    @Query("DELETE FROM watchlist WHERE owner = :owner AND movieId = :movieId")
    suspend fun remove(owner: String, movieId: String)

    /** oznacz jako oglądane, zachowując timestamp = teraz */
    @Query("""
      UPDATE watchlist 
      SET status = 'WATCHED', timestamp = :now 
      WHERE owner = :owner AND movieId = :movieId
    """)
    suspend fun markWatched(owner: String, movieId: String, now: Long)

    /** znajdź konkretny wpis */
    @Query("SELECT * FROM watchlist WHERE owner = :owner AND movieId = :movieId LIMIT 1")
    suspend fun find(owner: String, movieId: String): WatchlistEntity?

    /** lista TODO */
    @Query("""
      SELECT * FROM watchlist 
      WHERE owner = :owner AND status = 'TODO' 
      ORDER BY timestamp DESC
    """)
    suspend fun watchList(owner: String): List<WatchlistEntity>

    /** lista WATCHED */
    @Query("""
      SELECT * FROM watchlist 
      WHERE owner = :owner AND status = 'WATCHED' 
      ORDER BY timestamp DESC
    """)
    suspend fun watched(owner: String): List<WatchlistEntity>

    @Query("SELECT COUNT(*) FROM watchlist WHERE owner = :owner AND status = 'WATCHED'")
    suspend fun watchedCount(owner: String): Int
}