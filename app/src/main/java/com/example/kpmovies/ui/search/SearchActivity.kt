package com.example.kpmovies.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    // repozytorium filmów: sieć + cache
    private val repo by lazy {
        MovieRepository(
            RetrofitBuilder.omdb,
            AppDatabase.get(this).movieDao()
        )
    }

    // adapter RecyclerView
    private val adapter = MovieAdapter { movie ->
        // kliknięcie na element listy: przejdź do MovieDetailsActivity
        val intent = Intent(this, MovieDetailsActivity::class.java)
            .putExtra("id", movie.id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ustaw RecyclerView
        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        binding.rvMovies.adapter = adapter

        // Najpierw pokaż losowy film (lub pustą listę)
        lifecycleScope.launch {
            loadRandom()
        }

        // Podłącz listener do EditText tak jak w AddFriendsActivity:
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty()
            lifecycleScope.launch {
                performSearch(query)
            }
        }
    }

    /** Załaduj losowy film (dodatkowa funkcja, analogicznie jak w SearchActivity wcześniej) */
    private suspend fun loadRandom() {
        val randomMovie = repo.random()
        if (randomMovie != null) {
            withContext(Dispatchers.Main) {
                adapter.submitList(listOf(randomMovie))
            }
        } else {
            // można wyczyścić listę: adapter.submitList(emptyList())
        }
    }

    /** Wyszukiwanie po zapytaniu q */
    private suspend fun performSearch(q: String) {
        // Możesz ewentualnie pominąć wywołanie, gdy q jest puste:
        // if (q.isBlank()) { adapter.submitList(emptyList()); return }
        val list = repo.search(q)
        withContext(Dispatchers.Main) {
            adapter.submitList(list)
        }
    }
}