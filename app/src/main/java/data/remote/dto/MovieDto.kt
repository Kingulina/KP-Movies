package com.example.kpmovies.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDto(
        @Json(name = "imdbID") val id: String,
        @Json(name = "Title")  val title: String,
        @Json(name = "Poster") val poster: String,
        @Json(name = "Plot")   val plot: String,
        @Json(name = "Genre")  val genre: String,
        @Json(name = "Released") val released: String,
        @Json(name = "Director") val director: String
)
