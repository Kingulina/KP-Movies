package com.example.kpmovies.data.local.entity

import androidx.room.*

@Entity( tableName = "reviews")   //  ← **mnoga forma, jak w DAO**
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    /** identyczne nazwy jak w zapytaniach DAO */
    @ColumnInfo(name = "movieId")  val movieId: String,
    @ColumnInfo(name = "author")   val author : String,
    @ColumnInfo(name = "rating")   val rating : Int,
    @ColumnInfo(name = "text")     val text   : String,

    /** znacznik czasu – ‘created’, nie ‘timestamp’ */
    @ColumnInfo(name = "created")  val created: Long = System.currentTimeMillis()
)
