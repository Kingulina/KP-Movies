package com.example.kpmovies.data.remote   // <-- nic więcej

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.kpmovies.data.remote.dto.MovieDto
import com.example.kpmovies.data.remote.dto.SearchDto
import com.example.kpmovies.BuildConfig

interface OmdbService {
    @GET("/")
    suspend fun search(
        @Query("apikey") key: String = BuildConfig.OMDB_KEY,
        @Query("s") query: String,
        @Query("type") type: String = "movie"
    ): SearchDto          // {"Search":[…]}

    @GET("/")
    suspend fun details(
        @Query("apikey") key: String = BuildConfig.OMDB_KEY,
        @Query("i") imdbId: String,
        @Query("plot") plot: String = "full"
    ): MovieDto           // pełne info
}

