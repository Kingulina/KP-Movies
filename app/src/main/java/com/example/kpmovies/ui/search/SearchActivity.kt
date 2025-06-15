package com.example.kpmovies.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kpmovies.data.repository.MovieRepository
import com.example.kpmovies.data.remote.RetrofitBuilder
import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.databinding.ActivitySearchBinding
import com.example.kpmovies.ui.adapter.MovieAdapter
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import com.example.kpmovies.ui.details.MovieDetailsActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var b: ActivitySearchBinding
    private val repo by lazy {
        MovieRepository(
            RetrofitBuilder.omdb,
            AppDatabase.get(this).movieDao()
        )
    }

    private val adapter = MovieAdapter { movie ->
        startActivity(Intent(this, MovieDetailsActivity::class.java)
            .putExtra("id", movie.id))
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(b.root)

        // recycler
        b.rvMovies.layoutManager = LinearLayoutManager(this)
        b.rvMovies.adapter = adapter

        // pierwszy losowy film
        lifecycleScope.launch { loadRandom() }

        // search listener
        b.searchBar.editText?.addTextChangedListener { t ->
            lifecycleScope.launch { search(t.toString()) }
        }
    }

    private suspend fun loadRandom() {
        val m = repo.random() ?: return
        adapter.submitList(listOf(m))
    }

    private suspend fun search(q: String) {
        val list = repo.search(q)
        withContext(Main) { adapter.submitList(list) }
    }
}