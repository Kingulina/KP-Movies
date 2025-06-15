package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kpmovies.data.user.AppDatabase
import com.example.kpmovies.data.user.FriendEntity
import com.example.kpmovies.databinding.ActivityFriendProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.kpmovies.SessionManager
import android.widget.TextView

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var me: String
    private lateinit var friend: String
    private val dao by lazy { AppDatabase.get(applicationContext).friendDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ── Parametry przekazane w Intent ── */
        me     = SessionManager.getLogin(this) ?: ""          // ⬅︎ z SharedPrefs
        friend = intent.getStringExtra("friend") ?: ""

        binding.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname).text = me

        /* ── UI podstawowe ── */
        binding.tvUsername.text = friend
        binding.recyclerRecentlyWatched.layoutManager = LinearLayoutManager(this)

        /* ── PLUS / KOSZ ── */
        lifecycleScope.launch { refreshIcon() }
        binding.ivAction.setOnClickListener {
            lifecycleScope.launch {
                if (dao.isFriend(me, friend)) {
                    dao.remove(FriendEntity(owner = me, followee = friend))
                } else {
                    dao.add(FriendEntity(owner = me, followee = friend))
                }
                refreshIcon()
            }
        }

        /* ─────────────────────
           ▼▼  DOLNA NAWIGACJA  ▼▼
           ───────────────────── */
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java)); finish()
                }
                R.id.nav_search -> toast("Search – demo")
                R.id.nav_menu   -> toggleDrawer()
            }
            true
        }
        // nic nie podświetlamy – jesteśmy „poza” głównymi zakładkami

        /* ───────── DrawerMenu ───────── */
        binding.navView.setNavigationItemSelectedListener { m ->
            when (m.itemId) {
                R.id.nav_homepage -> {
                    startActivity(Intent(this, HomeActivity::class.java)); finish()
                }
                R.id.nav_watchlist -> startActivity(Intent(this, WatchListActivity::class.java))

                R.id.nav_friends   -> startActivity(Intent(this, FriendListActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

        /* ── Logout w headerze Drawer ── */
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

        /* ── przezroczysty scrim ── */
        binding.drawerLayout.setScrimColor(0x66000000)
    }

    /* --------------  Funkcje pomocnicze  -------------- */

    private fun toggleDrawer() = with(binding.drawerLayout) {
        if (isDrawerOpen(GravityCompat.END)) closeDrawer(GravityCompat.END)
        else                                 openDrawer(GravityCompat.END)
    }

    private suspend fun refreshIcon() {
        val isFriend = dao.isFriend(me, friend)
        withContext(Dispatchers.Main) {
            binding.ivAction.setImageResource(
                if (isFriend) R.drawable.baseline_delete_forever_24 else R.drawable.baseline_add_24
            )
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END))
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        else
            super.onBackPressed()
    }
}
