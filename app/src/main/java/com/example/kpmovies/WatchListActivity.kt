package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.databinding.ActivityWatchListBinding
import com.example.kpmovies.ui.adapter.MovieAdapter
import com.example.kpmovies.ui.details.MovieDetailsActivity
import com.example.kpmovies.ui.search.SearchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchListActivity : AppCompatActivity() {

    private lateinit var b: ActivityWatchListBinding
    private val me by lazy { SessionManager.getLogin(this) ?: "" }
    private lateinit var adapter: MovieAdapter
    private val db by lazy { AppDatabase.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityWatchListBinding.inflate(layoutInflater)
        setContentView(b.root)

        // ─── Nick w headerze ─────────────────────────────────
        b.tvNickname.text = me
        b.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname)
            .text = me

        adapter = MovieAdapter { movie ->
            startActivity(
                Intent(this, MovieDetailsActivity::class.java)
                    .putExtra("id", movie.id)
            )
        }
        b.rvList.layoutManager = GridLayoutManager(this, 2)
        b.rvList.adapter = adapter


        b.tabsGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                updateWatchedCount()
                loadTab(checkedId == R.id.btnWatchList)
            }
        }

        b.tabsGroup.check(R.id.btnWatchList)
        updateWatchedCount()
        loadTab(isWatchList = true)


        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home   ->
                    startActivity(Intent(this, HomeActivity::class.java)).also { finish() }
                R.id.nav_search ->
                    startActivity(Intent(this, SearchActivity::class.java))
                R.id.nav_menu   -> toggleDrawer()
            }
            true
        }
        b.bottomNav.menu.findItem(R.id.nav_menu).isChecked = true

        b.navView.setNavigationItemSelectedListener { m ->
            when(m.itemId) {
                R.id.nav_homepage  ->
                    startActivity(Intent(this, HomeActivity::class.java)).also { finish() }
                R.id.nav_browse    ->
                    startActivity(Intent(this, SearchActivity::class.java))
                R.id.nav_friends   ->
                    startActivity(Intent(this, FriendListActivity::class.java))
                R.id.nav_settings  ->
                    startActivity(Intent(this, SettingsActivity::class.java))
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
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
                )
                finish()
            }
    }

    private fun loadTab(isWatchList: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            val entries = if (isWatchList) {
                db.watchlistDao().watchList(me)
            } else {
                db.watchlistDao().watched(me)
            }

            val movies = entries.mapNotNull { db.movieDao().one(it.movieId) }
            withContext(Dispatchers.Main) {
                adapter.submitList(movies)
            }
        }
    }
    private fun updateWatchedCount() {
        lifecycleScope.launch(Dispatchers.IO) {
            val count = db.watchlistDao().watched(me).size
            withContext(Dispatchers.Main) {
                b.btnWatched.text = getString(R.string.watched_with_count, count)
            }
        }
    }

    private fun toggleDrawer() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.END))
            b.drawerLayout.closeDrawer(GravityCompat.END)
        else
            b.drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onBackPressed() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.END))
            b.drawerLayout.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
    }
}