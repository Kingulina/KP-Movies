package com.example.kpmovies.ui.details

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.kpmovies.ui.adapters.ReviewAdapter
import com.example.kpmovies.ui.search.SearchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var b: ActivityMovieDetailsBinding
    private val imdbId   by lazy { intent.getStringExtra("id")!! }
    private val db       by lazy { AppDatabase.get(this) }
    private val repo     by lazy { MovieRepository(RetrofitBuilder.omdb, db.movieDao()) }
    private val watchDao by lazy { db.watchlistDao() }
    private val revDao   by lazy { db.reviewDao() }
    private val me       by lazy { SessionManager.getLogin(this) ?: "" }

    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)

        // ─── Recyclerview recenzji ─────────────────────
        reviewAdapter = ReviewAdapter()
        b.rvReviews.layoutManager = LinearLayoutManager(this)
        b.rvReviews.adapter = reviewAdapter

        // ─── Załaduj wszystkie dane ────────────────────
        lifecycleScope.launch {
            loadDetails()
            refreshAvg()
            refreshReviews()
            refreshWatchIcon()
        }

        // ─── Toggle “do obejrzenia” (TODO / remove) ────
        b.btnAddWatch.setOnClickListener {
            lifecycleScope.launch {
                val entry = watchDao.find(me, imdbId)
                if (entry == null) {
                    // nie było wcześniej → dodaj TODO
                    watchDao.upsert(
                        WatchlistEntity(
                            owner = me,
                            movieId = imdbId,
                            status = "TODO",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } else if (entry.status == "TODO") {
                    // było TODO → usuń
                    watchDao.remove(me, imdbId)
                }
                // jeśli WATCHED → nic
                refreshWatchIcon()
            }
        }

        // ─── Zapis recenzji (zawsze WATCHED) ───────────
        b.btnSaveReview.setOnClickListener {
            val rating = b.ratingBar.rating.toInt()
            val text   = b.etReview.text.toString()
            lifecycleScope.launch {
                revDao.add(
                    ReviewEntity(
                        movieId = imdbId,
                        author = me,
                        rating = rating,
                        text = text
                    )
                )
                // przełóż na WATCHED (nadpisze jeśli był TODO)
                watchDao.upsert(
                    WatchlistEntity(
                        owner = me,
                        movieId = imdbId,
                        status = "WATCHED",
                        timestamp = System.currentTimeMillis()
                    )
                )
                // odśwież wszystko
                loadDetails()
                refreshAvg()
                refreshReviews()
                refreshWatchIcon()
            }
        }

        // ─── Drawer + bottom navigation ───────────────
        val nick = me
        b.tvNickname.text = nick
        b.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname)
            .text = nick

        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home   -> startActivity(Intent(this, HomeActivity::class.java)).also{ finish() }
                R.id.nav_search -> startActivity(Intent(this, SearchActivity::class.java))
                R.id.nav_menu   -> toggleDrawer()
            }
            true
        }
        b.navView.setNavigationItemSelectedListener { m ->
            when (m.itemId) {
                R.id.nav_homepage   -> startActivity(Intent(this, HomeActivity::class.java)).also{ finish() }
                R.id.nav_watchlist  -> startActivity(Intent(this, WatchListActivity::class.java))
                R.id.nav_friends    -> startActivity(Intent(this, FriendListActivity::class.java)) // tu FriendListActivity
                R.id.nav_settings   -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_browse     -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    b.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
            b.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
        b.navView.getHeaderView(0)
            .findViewById<ImageView>(R.id.btnLogout)
            .setOnClickListener {
                SessionManager.clear(this)
                startActivity(
                    Intent(this, LoginActivity::class.java)
                        .apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                finish()
            }
        b.drawerLayout.setScrimColor(0x66000000)
    }

    private suspend fun loadDetails() {
        try {
            val dto = repo.details(imdbId)
            withContext(Dispatchers.Main) {
                b.tvTitle.text    = dto.title
                b.tvPlot.text     = dto.plot
                b.tvGenre.text    = "Category:  ${dto.genre}"
                b.tvReleased.text = "Premiere:  ${dto.released}"
                b.tvDirector.text = "Direction : ${dto.director}"
                Glide.with(this@MovieDetailsActivity)
                    .load(dto.poster)
                    .placeholder(R.drawable.placeholder)
                    .into(b.ivPoster)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Brak internetu", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun refreshAvg() {
        val avg = revDao.avg(imdbId)
        withContext(Dispatchers.Main) {
            b.tvAvgRating.text = avg?.let { "☆ %.1f".format(it) } ?: "–"
        }
    }

    private suspend fun refreshReviews() {
        val list = revDao.forMovie(imdbId)
        withContext(Dispatchers.Main) {
            reviewAdapter.submitList(list)
        }
    }

    private suspend fun refreshWatchIcon() {
        val entry = watchDao.find(me, imdbId)
        withContext(Dispatchers.Main) {
            when {
                entry == null -> {
                    b.btnAddWatch.setImageResource(R.drawable.baseline_add_24)
                    b.btnAddWatch.isEnabled = true
                    b.btnAddWatch.imageTintList =
                    ContextCompat.getColorStateList(this@MovieDetailsActivity, R.color.black)
                }
                entry.status == "TODO" -> {
                    b.btnAddWatch.setImageResource(R.drawable.baseline_check_circle_24)
                    b.btnAddWatch.isEnabled = true
                    b.btnAddWatch.imageTintList =
                    ContextCompat.getColorStateList(this@MovieDetailsActivity, R.color.black)
                }
                entry.status == "WATCHED" -> {
                    b.btnAddWatch.setImageResource(R.drawable.baseline_check_circle_24)
                    b.btnAddWatch.isEnabled = false
                    b.btnAddWatch.imageTintList =
                        ContextCompat.getColorStateList(this@MovieDetailsActivity, R.color.purple_200)
                }
            }
        }
    }

    private fun toggleDrawer() = with(b.drawerLayout) {
        if (isDrawerOpen(GravityCompat.END)) closeDrawer(GravityCompat.END)
        else                                 openDrawer(GravityCompat.END)
    }

    override fun onBackPressed() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.END))
            b.drawerLayout.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
    }
}