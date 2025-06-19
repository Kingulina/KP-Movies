package com.example.kpmovies.data.repository

import com.example.kpmovies.data.local.dao.MovieDao
import com.example.kpmovies.data.local.entity.MovieEntity
import com.example.kpmovies.data.remote.OmdbService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(
    private val remote: OmdbService,
    private val movieDao: MovieDao
) {
    /** wyszukiwanie on-line + cache do Room */
    suspend fun search(query: String): List<MovieEntity> = withContext(Dispatchers.IO) {
        val net = remote.search(query = query).search.orEmpty()
        val entities = net.map { MovieEntity(it.imdbId, it.title, it.poster ?: "") }
        movieDao.insertAll(entities)
        movieDao.searchLocal("%$query%")
    }

    /** losowy film (literka A…Z) */
    suspend fun random(): MovieEntity? = withContext(Dispatchers.IO) {
        val letter = ('A'..'Z').random().toString()
        search(letter).randomOrNull()
    }

    /** pełne szczegóły */
    suspend fun details(imdbId: String) = remote.details(imdbId = imdbId)
}