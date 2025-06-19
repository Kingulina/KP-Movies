package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.data.user.FriendEntity
import com.example.kpmovies.databinding.ActivityFriendProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.TextView
import com.example.kpmovies.ui.search.SearchActivity
import com.example.kpmovies.ui.adapters.FriendRecentAdapter
import com.example.kpmovies.ui.adapters.FriendRecentItem
import com.example.kpmovies.ui.details.MovieDetailsActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FriendProfileActivity : AppCompatActivity() {


    private lateinit var b: ActivityFriendProfileBinding
    private lateinit var me:     String
    private lateinit var friend: String

    private val db   by lazy { AppDatabase.get(this) }
    private val fDao by lazy { db.friendDao() }

    /** adapter do poziomego paska „Recently watched” */
    private lateinit var recentAdapter: FriendRecentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(b.root)

        me     = SessionManager.getLogin(this) ?: ""
        friend = intent.getStringExtra("friend") ?: ""

        b.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname).text = me

        b.tvUsername.text = friend

        recentAdapter = FriendRecentAdapter { openMovie(it.movieId) }

        b.recyclerRecentlyWatched.apply {
            layoutManager = GridLayoutManager(
                this@FriendProfileActivity,
                2,                         // spanCount = 2 kolumny
                RecyclerView.VERTICAL,
                false
            )
            adapter        = recentAdapter

            val pad = resources.getDimensionPixelSize(R.dimen.dp16)
            setPadding(pad, pad, pad, pad)
            clipToPadding = false
        }

        /* ─── ikonka „dodaj / usuń znajomego” ─── */
        lifecycleScope.launch { refreshIcon() }
        b.ivAction.setOnClickListener {
            lifecycleScope.launch {
                if (fDao.isFriend(me, friend))
                    fDao.remove(FriendEntity(me, friend))
                else
                    fDao.add   (FriendEntity(me, friend))
                refreshIcon()
            }
        }
        me = SessionManager.getLogin(this) ?: ""
        b.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname).text = me
        setupNavigation()

        /* ─── wczytaj listę ostatnio obejrzanych filmów ─── */
        lifecycleScope.launch {
            loadRecentlyWatched()
            loadStats()
        }

        b.drawerLayout.setScrimColor(0x66000000)
    }

    private fun openMovie(id: String) =
        startActivity(Intent(this, MovieDetailsActivity::class.java).putExtra("id", id))

    private fun loadStats() = lifecycleScope.launch(Dispatchers.IO) {
        val moviesCnt = db.watchlistDao().watchedCount(friend)
        withContext(Dispatchers.Main) {
            b.tvMoviesWatched.text = moviesCnt.toString()
        }
    }

    /** odśwież iconkę „+ / kosz” */
    private suspend fun refreshIcon() {
        val isFriend = fDao.isFriend(me, friend)
        withContext(Dispatchers.Main) {
            b.ivAction.setImageResource(
                if (isFriend) R.drawable.baseline_delete_forever_24
                else           R.drawable.baseline_add_24
            )
        }
    }

    /** pobiera ostatnie recenzje przyjaciela i wyświetla je w pasku */
    private fun loadRecentlyWatched() = lifecycleScope.launch {

        val reviews = withContext(Dispatchers.IO) {
            AppDatabase.get(this@FriendProfileActivity)
                .reviewDao()
                .latestByUsers(listOf(friend), 30)
        }


        val items = reviews.mapNotNull { rev ->
            val m = AppDatabase.get(this@FriendProfileActivity)
                .movieDao()
                .one(rev.movieId) ?: return@mapNotNull null

            FriendRecentItem(
                movieId    = rev.movieId,
                posterUrl  = m.poster,
                movieTitle = m.title,
                rating     = rev.rating
            )
        }

        recentAdapter.submitList(items)
    }

    private fun toggleDrawer() = with(b.drawerLayout) {
        if (isDrawerOpen(GravityCompat.END)) closeDrawer(GravityCompat.END)
        else                                 openDrawer(GravityCompat.END)
    }

    private fun setupNavigation() {
        b.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home   -> { startActivity(Intent(this, HomeActivity::class.java)); finish() }
                R.id.nav_search ->   startActivity(Intent(this, SearchActivity::class.java))
                R.id.nav_menu   ->   toggleDrawer()
            }; true
        }
        b.navView.setNavigationItemSelectedListener { m ->
            when (m.itemId) {
                R.id.nav_homepage  -> { startActivity(Intent(this, HomeActivity::class.java)); finish() }
                R.id.nav_watchlist ->   startActivity(Intent(this, WatchListActivity::class.java))
                R.id.nav_friends   ->   startActivity(Intent(this, FriendListActivity::class.java))
                R.id.nav_settings  ->   startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_browse    ->   startActivity(Intent(this, SearchActivity::class.java))
            }
            b.drawerLayout.closeDrawer(GravityCompat.END); true
        }
        b.navView.getHeaderView(0)
            .findViewById<ImageView>(R.id.btnLogout)
            .setOnClickListener {
                SessionManager.clear(this)
                startActivity(
                    Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                ); finish()
            }
    }

    override fun onBackPressed() {
        if (b.drawerLayout.isDrawerOpen(GravityCompat.END))
            b.drawerLayout.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}