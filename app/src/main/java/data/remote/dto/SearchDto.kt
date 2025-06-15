

package com.example.kpmovies.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchDto(
        @Json(name = "Search") val Search: List<SearchItemDto>?
)