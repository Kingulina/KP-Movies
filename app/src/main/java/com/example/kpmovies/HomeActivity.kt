package com.example.kpmovies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // demo – pokaż nick przekazany z LoginActivity (jeśli był)
        binding.tvNickname.text = intent.getStringExtra("nick") ?: "Nickname"

        // proste reakcje na klik w dolnej nawigacji
        binding.bottomNav.setOnItemSelectedListener(navListener)
    }

    private val navListener = NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home   -> toast("Home")
            R.id.nav_search -> toast("Search clicked (todo)")
            R.id.nav_menu   -> toast("Menu clicked (todo)")
        }
        true
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
