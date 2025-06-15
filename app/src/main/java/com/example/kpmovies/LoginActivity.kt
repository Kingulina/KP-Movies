package com.example.kpmovies

import com.example.kpmovies.data.user.AppDatabase // tylko w RegisterActivity
import com.example.kpmovies.data.user.UserDao
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivityLoginBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                lifecycleScope.launch {
                    val userDao = AppDatabase.get(applicationContext).userDao()
                    val user = userDao.getUser(login, pass)

                    withContext(Dispatchers.Main) {
                        if (user == null) {
                            toast("Błędny login lub hasło")
                        } else {
                            SessionManager.saveLogin(this@LoginActivity, login)

                            /* putExtra możesz zostawić lub usunąć – nie będzie już potrzebny */
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        }
                    }
                }
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