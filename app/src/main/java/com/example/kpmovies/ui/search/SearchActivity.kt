package com.example.kpmovies.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.data.remote.RetrofitBuilder
import com.example.kpmovies.data.repository.MovieRepository
import com.example.kpmovies.databinding.ActivitySearchBinding
import com.example.kpmovies.ui.adapter.MovieAdapter
import com.example.kpmovies.ui.details.MovieDetailsActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var b: ActivitySearchBinding

    private val repo by lazy {
        MovieRepository(
            RetrofitBuilder.omdb,
            AppDatabase.get(this).movieDao()
        )
    }

    private val adapter = MovieAdapter { movie ->
        startActivity(
            Intent(this, MovieDetailsActivity::class.java)
                .putExtra("id", movie.id)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.rvMovies.layoutManager = LinearLayoutManager(this)
        b.rvMovies.adapter       = adapter

        lifecycleScope.launch { loadRandom() }

        /* listener wyszukiwarki */
        b.searchBar.editText?.addTextChangedListener { txt ->
            lifecycleScope.launch { search(txt.toString()) }
        }
    }

    private suspend fun loadRandom() {
        repo.random()?.let { withContext(Main) { adapter.submitList(listOf(it)) } }
    }

    private suspend fun search(q: String) {
        val list = repo.search(q)
        withContext(Main) { adapter.submitList(list) }
    }
}