package com.example.kpmovies.data.local.dao

import androidx.room.*
import com.example.kpmovies.data.local.entity.ReviewEntity

@Dao
interface ReviewDao {

    /* recenzje konkretnego filmu */
    @Query(
        """
        SELECT *
        FROM reviews              -- ⬅ tabela w liczbie pojedynczej
        WHERE movieId = :movieId
        ORDER BY created DESC    -- ⬅ kolumna  `created`
        """
    )
    suspend fun forMovie(movieId: String): List<ReviewEntity>

    /* średnia ocen */
    @Query(
        """
        SELECT AVG(rating * 1.0)
        FROM reviews
        WHERE movieId = :movieId
        """
    )
    suspend fun avg(movieId: String): Double?

    /* dodaj / nadpisz (1-recenzja-na-użytkownika) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(review: ReviewEntity)

    /* usuń recenzję użytkownika */
    @Query(
        """
        DELETE FROM reviews
        WHERE movieId = :movieId
          AND author  = :author
        """
    )
    suspend fun remove(movieId: String, author: String)

    /* ostatnie aktywności znajomych */
    @Query(
        """
        SELECT *
        FROM reviews
        WHERE author IN (:authors)
        ORDER BY created DESC
        LIMIT :limit
        """
    )
    suspend fun latestByUsers(
        authors: List<String>,
        limit:   Int
    ): List<ReviewEntity>
}
