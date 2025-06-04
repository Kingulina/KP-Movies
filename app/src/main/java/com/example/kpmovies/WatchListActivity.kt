package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.kpmovies.databinding.ActivityWatchListBinding

class WatchListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ───────── 1. Nickname przekazany z Home ───────── */
        val nick = intent.getStringExtra("nick") ?: "Nickname"
        binding.tvNickname.text = nick
        // jeśli w DrawerHeader też chcesz nick:
        binding.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname).text = nick

        /* ───────── 2. Taby Watched / Watch list ───────── */
        selectTab(isWatchList = true)
        binding.btnWatchList.setOnClickListener { selectTab(true) }
        binding.btnWatched.setOnClickListener  { selectTab(false) }

        /* ───────── 3. Ikona filtra ───────── */
        binding.ivFilter.setOnClickListener { toast("Filter – demo") }

        /* ───────── 4. Dolna nawigacja ───────── */
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home   -> finish()                 // wróć do Home
                R.id.nav_search -> toast("Search – demo")
                R.id.nav_menu   -> toggleDrawer()           // otwórz / zamknij Drawer
            }
            true
        }
        // Podświetlamy ikonę Menu jako aktywną
        binding.bottomNav.menu.findItem(R.id.nav_menu).isChecked = true

        /* ───────── 5. Itemy w DrawerMenu ───────── */
        binding.navView.setNavigationItemSelectedListener { m ->
            when (m.itemId) {
                R.id.nav_homepage -> finish()   // wracamy do HomeActivity
                R.id.nav_settings  -> startActivity(Intent(this, SettingsActivity::class.java))
                // Watch list (nav_watchlist) nic nie robi – jesteśmy tu
            }
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

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

        /* ───────── 6. Przezroczysty scrim ───────── */
        binding.drawerLayout.setScrimColor(0x66000000)
    }

    /* --------------  Funkcje pomocnicze  -------------- */

    private fun selectTab(isWatchList: Boolean) {
        binding.btnWatchList.isChecked = isWatchList
        binding.btnWatched.isChecked   = !isWatchList
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END))
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        else
            binding.drawerLayout.openDrawer(GravityCompat.END)
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
