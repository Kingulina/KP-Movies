package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.kpmovies.databinding.ActivityFriendListBinding

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* 1️⃣  ustaw nick z Intentu (jeśli jest) */
        intent.getStringExtra("nick")?.let { nick ->
            val header = binding.navView.getHeaderView(0)
            header.findViewById<com.google.android.material.textview.MaterialTextView>(
                R.id.drawerNickname
            ).text = nick
        }

        /* 2️⃣  przycisk „+” – demo */
        binding.ivAddFriend.setOnClickListener {
            Toast.makeText(this, "Add friend – TODO", Toast.LENGTH_SHORT).show()
        }

        /* 3️⃣  dolna nawigacja */
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                R.id.nav_search -> toast("Search – demo")
                R.id.nav_menu  -> toggleDrawer()
            }
            true
        }
        // podświetlamy lupkę, bo jesteśmy na „środkowym” ekranie
        binding.bottomNav.menu.findItem(R.id.nav_search).isChecked = true

        /* 4️⃣  DrawerMenu */
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_homepage  -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_watchlist -> startActivity(Intent(this, WatchListActivity::class.java))
                R.id.nav_settings  -> startActivity(Intent(this, SettingsActivity::class.java))
                // nav_friends – jesteśmy już tutaj
            }
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

        /* 5️⃣  Logout w headerze Drawer */
        binding.navView.getHeaderView(0)
            .findViewById<ImageView>(R.id.btnLogout)
            .setOnClickListener {
                startActivity(
                    Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }
    }

    private fun toggleDrawer() = with(binding.drawerLayout) {
        if (isDrawerOpen(GravityCompat.END))
            closeDrawer(GravityCompat.END)
        else
            openDrawer(GravityCompat.END)
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
