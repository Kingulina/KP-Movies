package com.example.kpmovies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivityAddFriendsBinding
import com.example.kpmovies.ui.UserAdapter
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.kpmovies.data.local.AppDatabase
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager



class AddFriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFriendsBinding
    private lateinit var me: String
    private val adapter = UserAdapter { user ->
        startActivity(
            Intent(this, FriendProfileActivity::class.java)
                .putExtra("friend", user.login)    // ← 'me' już niepotrzebne
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        me = SessionManager.getLogin(this) ?: ""
        binding.recycler.layoutManager = LinearLayoutManager(this)

        binding.recycler.adapter = adapter
        loadUsers("")

        binding.etSearch.addTextChangedListener { txt ->
            loadUsers(txt.toString())
        }
    }

    /** pobiera i wstawia listę użytkowników zawierających query */
    private fun loadUsers(query: String) {
        lifecycleScope.launch {
            val list = AppDatabase.get(applicationContext)
                .userDao()
                .searchUsers(query, me)          // ← filtruje „nie pokazuj mnie”
            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }
    }
}