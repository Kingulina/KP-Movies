package com.example.kpmovies.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kpmovies.SessionManager
import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.data.local.entity.ReviewEntity
import com.example.kpmovies.data.local.entity.WatchlistEntity
import com.example.kpmovies.data.remote.RetrofitBuilder
import com.example.kpmovies.data.repository.MovieRepository
import com.example.kpmovies.databinding.ActivityMovieDetailsBinding
import kotlinx.coroutines.launch

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var b: ActivityMovieDetailsBinding
    private val imdbId by lazy { intent.getStringExtra("id")!! }

    private val repo      by lazy { MovieRepository(RetrofitBuilder.omdb, AppDatabase.get(this).movieDao()) }
    private val watchDao  by lazy { AppDatabase.get(this).watchlistDao() }
    private val reviewDao by lazy { AppDatabase.get(this).reviewDao() }
    private val me        by lazy { SessionManager.getLogin(this) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)

        lifecycleScope.launch {
            /* TODO: pobierz dto = repo.details(imdbId) i wypełnij UI */
        }
    }
}