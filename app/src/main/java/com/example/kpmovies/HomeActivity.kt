package com.example.kpmovies

import android.content.Intent
import android.graphics.Color          // <-- NOWY import
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.kpmovies.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ►► przezroczysty scrim ◄◄ */
        binding.drawerLayout.setScrimColor(0x66000000)

        /* 1. Nickname przekazany z LoginActivity (opcjonalnie) */
        intent.getStringExtra("nick")?.let { nick ->
            binding.tvNickname.text = nick
            val header = binding.navView.getHeaderView(0)
            header.findViewById<TextView>(R.id.drawerNickname).text = nick
        }

        /* 2. Dolna nawigacja */
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home   -> { /* już jesteśmy na Home */ }
                R.id.nav_search -> { /* TODO: Search */ }
                R.id.nav_menu   ->  toggleDrawer()
            }
            true
        }

        /* 3. Kliknięcia pozycji w DrawerMenu */
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_homepage -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                /* pozostałe: nav_watchlist, nav_friends, nav_browse, nav_settings -> na razie puste */
                R.id.nav_settings  -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                R.id.nav_watchlist -> {
                    val nick = binding.tvNickname.text.toString()
                    startActivity(
                        Intent(this, WatchListActivity::class.java).apply {
                            putExtra("nick", nick)
                        }
                    )
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                R.id.nav_friends -> {
                    startActivity(Intent(this, FriendListActivity::class.java))
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }
            true
        }

        /* 4. Wylogowanie w nagłówku Drawer */
        binding.navView.getHeaderView(0)
            .findViewById<ImageView>(R.id.btnLogout)
            .setOnClickListener {
                startActivity(
                    Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }
    }

    /* Otwórz / zamknij Drawer */
    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    /* Prost y Toast */
    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    /* Zamknij Drawer przy Back zamiast zamykać Activity */
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
}