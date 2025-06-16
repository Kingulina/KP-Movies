package com.example.kpmovies.ui.details

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.kpmovies.FriendListActivity
import com.example.kpmovies.HomeActivity
import com.example.kpmovies.LoginActivity
import com.example.kpmovies.R
import com.example.kpmovies.SessionManager
import com.example.kpmovies.SettingsActivity
import com.example.kpmovies.WatchListActivity
import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.data.local.entity.ReviewEntity
import com.example.kpmovies.data.local.entity.WatchlistEntity
import com.example.kpmovies.data.remote.RetrofitBuilder
import com.example.kpmovies.data.repository.MovieRepository
import com.example.kpmovies.databinding.ActivityMovieDetailsBinding
import com.example.kpmovies.ui.search.SearchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val imdbId by lazy { intent.getStringExtra("id") ?: "" }

    private val repo      by lazy { MovieRepository(RetrofitBuilder.omdb, AppDatabase.get(this).movieDao()) }
    private val watchDao  by lazy { AppDatabase.get(this).watchlistDao() }
    private val reviewDao by lazy { AppDatabase.get(this).reviewDao() }
    private val me        by lazy { SessionManager.getLogin(this) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // załaduj dane sieci + cache
        lifecycleScope.launch { loadDetails() }

        // klik "dodaj do watchlisty"
        binding.btnAddWatch.setOnClickListener { lifecycleScope.launch { toggleWatchlist() } }

        // zapis recenzji
        binding.btnSaveReview.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt()
            val text   = binding.etReview.text.toString()
            lifecycleScope.launch { saveReview(rating, text) }
        }

        val nick = SessionManager.getLogin(this) ?: "Nickname"
        binding.tvNickname.text = nick
        binding.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname).text = nick

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }     // wróć do Ho me
                R.id.nav_search -> startActivity(Intent(this, SearchActivity::class.java))
                R.id.nav_menu   -> toggleDrawer()           // otwórz / zamknij Drawer
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener { m ->
            when (m.itemId) {

                /* ——— Home ——— */
                R.id.nav_homepage -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()                      // zamykamy bieżące WatchListActivity
                }

                /* ——— Friends ——— */
                R.id.nav_friends -> {
                    startActivity(Intent(this, FriendListActivity::class.java)
                        .putExtra("nick", nick))   // ← teraz używamy nick z SessionManager
                    finish()                    // żeby po „back” nie wracać do watch-list
                }

                /* ——— Settings ——— */
                R.id.nav_settings -> startActivity(
                    Intent(this, SettingsActivity::class.java)
                )

                R.id.nav_watchlist -> {
                    val nick = binding.tvNickname.text.toString()
                    startActivity(Intent(this, WatchListActivity::class.java)
                        .putExtra("nick", nick))
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                R.id.nav_browse -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }

            /* zamknij Drawer niezależnie od wyboru */
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

        binding.navView.getHeaderView(0)
            .findViewById<ImageView>(R.id.btnLogout)
            .setOnClickListener {
                SessionManager.clear(this)
                startActivity(
                    Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }

        /* ───────── 6. Przezroczysty scrim ───────── */
        binding.drawerLayout.setScrimColor(0x66000000)


    }

    /* ---------------- helpery ---------------- */


    private suspend fun loadDetails() {
        try {
            val dto = repo.details(imdbId)
            withContext(Dispatchers.Main) {
                binding.tvTitle.text    = dto.title
                binding.tvPlot.text     = dto.plot
                binding.tvGenre.text    = "Category:  ${dto.genre}"
                binding.tvReleased.text = "Premiere:  ${dto.released}"
                binding.tvDirector.text = "Direction : ${dto.director}"

                Glide.with(this@MovieDetailsActivity)
                    .load(dto.poster)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.ivPoster)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Brak internetu", Toast.LENGTH_SHORT).show()
        }
        refreshAvg(); refreshReviews(); refreshWatchIcon()
    }

    private suspend fun toggleWatchlist() {
        val exists = watchDao.all(me).any { it.movieId == imdbId }
        if (exists) watchDao.remove(me, imdbId)
        else         watchDao.add(WatchlistEntity(me, imdbId,))
        refreshWatchIcon()
    }

    private suspend fun saveReview(rating: Int, text: String) {
        reviewDao.add(ReviewEntity(movieId = imdbId, author = me, rating = rating, text = text))
        refreshAvg(); refreshReviews(); refreshWatchIcon()
        withContext(Dispatchers.Main) {
            binding.etReview.setText("")
            binding.ratingBar.rating = 0f
        }
    }

    private suspend fun refreshAvg() {
        val avg = reviewDao.avg(imdbId) ?: return
        withContext(Dispatchers.Main) {
            binding.tvAvgRating.text = "☆ ${"%.1f".format(avg)}"
        }
    }


    private suspend fun refreshWatchIcon() {
        val exists = watchDao.all(me).any { it.movieId == imdbId }
        withContext(Dispatchers.Main) {
            binding.btnAddWatch.setImageResource(
                if (exists) R.drawable.baseline_check_circle_24 else R.drawable.baseline_add_24
            )
        }
    }
    private suspend fun refreshReviews() {
        val list = reviewDao.forMovie(imdbId)
        // adapter.submitList(list) – dodaj gdy masz adapter recenzji
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END))
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        else
            binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END))
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
    }
}
