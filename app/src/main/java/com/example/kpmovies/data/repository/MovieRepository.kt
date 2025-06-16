package com.example.kpmovies.data.repository

import com.example.kpmovies.data.local.dao.MovieDao
import com.example.kpmovies.data.local.entity.MovieEntity
import com.example.kpmovies.data.remote.OmdbService
import com.example.kpmovies.data.remote.dto.SearchItemDto

class MovieRepository(
    private val remote: OmdbService,
    private val movieDao: MovieDao
) {

    /** wyszukiwanie filmów po tytule */
    suspend fun search(query: String): List<MovieEntity> {
        // 1. pobierz z sieci
        val fromNet: List<SearchItemDto> =
            remote.search(query = query).Search ?: emptyList()

        // 2. DTO ➜ encje Room
        val entities = fromNet.map {
            MovieEntity(
                imdbId = it.id,        //  ←  `.id`
                title  = it.title,     //  ←  `.title`
                poster = it.poster     //  ←  `.poster`
            )
        }

        // 3. zapisz do cache
        movieDao.insertAll(entities)

        // 4. zwróć z lokalnej bazy (żeby korzystać z indeksów SQL)
        return movieDao.searchLocal(query)
    }

    /** losowy film dla ekranu Home */
    suspend fun random(): MovieEntity? {
        val randomLetter = ('A'..'Z').random().toString()
        val list = search(randomLetter)
        return list.randomOrNull()
    }

    /** szczegóły filmu */
    suspend fun details(id: String) =
        remote.details(id = id)        //  ←  parametr *id*
}
