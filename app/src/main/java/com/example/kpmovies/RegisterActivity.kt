package com.example.kpmovies

import com.example.kpmovies.data.local.AppDatabase
import com.example.kpmovies.data.user.UserEntity
import com.example.kpmovies.data.user.UserDao
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivityRegisterBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ========  Rejestracja (demo)  ======== */
        binding.btnRegister.setOnClickListener {
            val login  = binding.etLogin.text.toString()
            val pass   = binding.etPassword.text.toString()
            val repeat = binding.etRepeat.text.toString()

            when {
                login.isBlank() || pass.isBlank() || repeat.isBlank() ->
                    toast("Uzupełnij wszystkie pola")

                pass != repeat -> toast("Hasła nie są identyczne")

                else -> lifecycleScope.launch {
                    val userDao = AppDatabase.get(applicationContext).userDao()

                    if (userDao.exists(login)) {
                        withContext(Dispatchers.Main) { toast("Login już istnieje") }
                        return@launch
                    }

                    userDao.insert(UserEntity(login, pass))
                    withContext(Dispatchers.Main) {
                        toast("Zarejestrowano pomyślnie")
                        finish()              // wracamy do ekranu logowania
                    }
                }
            }
        }

        /* -------- powrót „Log in” -------- */
        binding.tvGoLogin.setOnClickListener { finish() }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
