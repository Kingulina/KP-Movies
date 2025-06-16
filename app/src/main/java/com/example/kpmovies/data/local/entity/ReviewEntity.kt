// app/src/main/java/com/example/kpmovies/data/local/entity/ReviewEntity.kt
package com.example.kpmovies.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /** identyfikator filmu z OMDb (pole imdbID) */
    @ColumnInfo(name = "movieId") val movieId: String,

    /** login autora recenzji */
    @ColumnInfo(name = "author")  val author: String,

    /** ocena 1-10 */
    @ColumnInfo(name = "rating")  val rating: Int,

    /** treść recenzji – może być pusta */
    @ColumnInfo(name = "text")    val text: String,

    /** znaczek czasu – potrzebny do sortowania */
    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis()
)
