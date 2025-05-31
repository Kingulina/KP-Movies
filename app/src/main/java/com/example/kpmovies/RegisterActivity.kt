package com.example.kpmovies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ========  Rejestracja (demo)  ======== */
        binding.btnRegister.setOnClickListener {
            val login   = binding.etLogin.text.toString()
            val pass    = binding.etPassword.text.toString()
            val repeat  = binding.etRepeat.text.toString()

            when {
                login.isBlank() || pass.isBlank() || repeat.isBlank() ->
                    toast("Uzupełnij wszystkie pola")

                pass != repeat ->
                    toast("Hasła nie są identyczne")

                else -> {
                    // Tu zapisz użytkownika do bazy lub Firebase
                    toast("Zarejestrowano")
                    finish()            // wracamy do ekranu logowania
                }
            }
        }

        /* -------- powrót „Log in” -------- */
        binding.tvGoLogin.setOnClickListener { finish() }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}