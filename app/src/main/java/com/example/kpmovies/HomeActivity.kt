package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.databinding.ActivityHomeBinding
import com.example.kpmovies.ui.adapter.RecentAdapter
import com.example.kpmovies.ui.adapter.RecentItem
import com.example.kpmovies.ui.details.MovieDetailsActivity
import com.example.kpmovies.ui.search.SearchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var b: ActivityHomeBinding
    private val db by lazy { AppDatabase.get(this) }
    private val scope by lazy { lifecycleScope }

    private lateinit var recentAdapter: RecentAdapter         // <- przyjmuje RecentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* ► pół-przezroczysty scrim Drawer --──────────────── */
        b.drawerLayout.setScrimColor(0x66000000)

        /* ► avatar + nick ─────────────────────────────────── */
        SessionManager.getLogin(this)?.let { nick ->
            b.tvNickname.text = nick
            b.navView.getHeaderView(0)
                .findViewById<TextView>(R.id.drawerNickname).text = nick
        }

        /* ► bottom nav ────────────────────────────────────── */
        b.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_search -> startActivity(Intent(this, SearchActivity::class.java))
                R.id.nav_menu   -> toggleDrawer()
            }
            true
        }

        /* ► drawer menu ───────────────────────────────────── */
        b.navView.setNavigationItemSelectedListener { m ->
            when (m.itemId) {
                R.id.nav_watchlist -> startActivity(Intent(this, WatchListActivity::class.java))
                R.id.nav_friends   -> startActivity(Intent(this, FriendListActivity::class.java))
                R.id.nav_browse    -> startActivity(Intent(this, SearchActivity::class.java))
                R.id.nav_settings  -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            b.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

        /* ► Logout w headerze ─────────────────────────────── */
        b.navView.getHeaderView(0)
            .findViewById<ImageView>(R.id.btnLogout)
            .setOnClickListener {
                SessionManager.clear(this)
                startActivity(
                    Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }

        /* ► sekcja: Today’s recommendations ———————————— */
        loadRecommendations()

        /* ► sekcja: Friends recent activity  ——————————— */
        recentAdapter = RecentAdapter { openMovie(it.movieId) }
        b.rvFriendsActivity.adapter = recentAdapter
        b.rvFriendsActivity.layoutManager = GridLayoutManager(this, 2)
        loadFriendsActivity()
    }

    /* ───────────────── helpery ───────────────────────────── */

    private fun openMovie(movieId: String) =
        startActivity(Intent(this, MovieDetailsActivity::class.java).putExtra("id", movieId))

    /** losuje DWA filmy z cache’u i wstawia do UI */
    private fun loadRecommendations() = scope.launch(Dispatchers.IO) {
        val allMovies = db.movieDao().all()
        if (allMovies.size < 2) return@launch

        val (m1, m2) = allMovies.shuffled().take(2)
        withContext(Dispatchers.Main) {
            // pierwszy
            Glide.with(this@HomeActivity)
                .load(m1.poster)
                .placeholder(R.drawable.placeholder)
                .into(b.imgRec1)
            b.tvRecTitle1.text = m1.title
            b.imgRec1.setOnClickListener { openMovie(m1.id) }

            // drugi
            Glide.with(this@HomeActivity)
                .load(m2.poster)
                .placeholder(R.drawable.placeholder)
                .into(b.imgRec2)
            b.tvRecTitle2.text = m2.title
            b.imgRec2.setOnClickListener { openMovie(m2.id) }
        }
    }

    /** pobiera ostatnie recenzje wystawione przez obserwowanych */
    private fun loadFriendsActivity() = scope.launch(Dispatchers.IO) {

        val me = SessionManager.getLogin(this@HomeActivity) ?: return@launch

        // ▼--- POPRAWKA: od razu dostajemy listę loginów
        val followees = db.friendDao().followsOf(me)          // już List<String>
        if (followees.isEmpty()) return@launch                // gdy nikogo nie obserwuję

        val reviews = db.reviewDao().latestByUsers(followees, limit = 50)

        /* mapujemy ReviewEntity → RecentItem */
        val items = reviews.mapNotNull { rev ->
            val movie = db.movieDao().one(rev.movieId) ?: return@mapNotNull null
            RecentItem(
                movieId    = rev.movieId,
                posterUrl  = movie.poster,
                movieTitle = movie.title,
                author     = rev.author,
                rating     = rev.rating
            )
        }

        withContext(Dispatchers.Main) { recentAdapter.submitList(items) }
    }

    private fun toggleDrawer() =
        if (b.drawerLayout.isDrawerOpen(GravityCompat.END))
            b.drawerLayout.closeDrawer(GravityCompat.END)
        else
            b.drawerLayout.openDrawer(GravityCompat.END)

    override fun onBackPressed() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.END))
            b.drawerLayout.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
    }
}
