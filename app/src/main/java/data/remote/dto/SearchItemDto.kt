package com.example.kpmovies.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchItemDto(
        @Json(name = "imdbID") val imdbId: String,
        @Json(name = "Title")  val title : String,
        @Json(name = "Poster") val poster: String?
)