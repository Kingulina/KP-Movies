package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding   // ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ========  OBSŁUGA PRZYCISKÓW  ======== */

        // 1️⃣  Logowanie (demo)
        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val pass  = binding.etPassword.text.toString()

            if (login.isBlank() || pass.isBlank()) {
                toast("Wpisz login i hasło")
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("nick", login)
                startActivity(intent)
                finish()
            }
        }

        // 2️⃣  Przejście do rejestracji
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /* ---------  Mała funkcja pomocnicza  ---------- */
    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}