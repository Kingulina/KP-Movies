package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.kpmovies.databinding.ActivityFriendListBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.ui.UserAdapter
import android.widget.TextView
import com.example.kpmovies.ui.search.SearchActivity


class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding
    private lateinit var me: String
    /**  adapter widoczny w całej klasie  */
    private val adapter = UserAdapter { user ->
        startActivity(
            Intent(this, FriendProfileActivity::class.java)
                .putExtra("me", me)
                .putExtra("friend", user.login)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        me = SessionManager.getLogin(this) ?: ""
        binding.navView.getHeaderView(0)
            .findViewById<TextView>(R.id.drawerNickname).text = me
        /* ↴  podpinamy adapter do RecyclerView (rvFriends) */
        binding.rvFriends.adapter = adapter
        binding.rvFriends.layoutManager = LinearLayoutManager(this)

        binding.ivAddFriend.setOnClickListener {
            startActivity(Intent(this, AddFriendsActivity::class.java))
        }


        /* ↴  ładujemy listę obserwowanych */
        lifecycleScope.launch {
            val dao  = AppDatabase.get(applicationContext).friendDao()
            val list = dao.getFriends(me)            // ← używamy zmiennej me
            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }

        /* 3️⃣  dolna nawigacja */
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                R.id.nav_search -> startActivity(Intent(this, SearchActivity::class.java))
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
                SessionManager.clear(this)
                startActivity(
                    Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }
    }

    override fun onResume() {
        super.onResume()
        loadFriends()          // <-- dodać
    }

    private fun loadFriends() {
        lifecycleScope.launch {
            val list = AppDatabase.get(applicationContext)
                .friendDao()
                .getFriends(me)           // 'me' masz już ustawione
            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
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
