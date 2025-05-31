package com.example.kpmovies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ========  Reset hasła (demo)  ======== */
        binding.btnReset.setOnClickListener {
            val newPass = binding.etNewPass.text.toString()
            val repeat  = binding.etRepeatPass.text.toString()

            when {
                newPass.isBlank() || repeat.isBlank() ->
                    toast("Wpisz nowe hasło dwukrotnie")

                newPass != repeat ->
                    toast("Hasła nie są identyczne")

                else -> {
                    // Tutaj wołasz backend / Firebase „update password”
                    toast("Hasło zresetowane – demo 🔑")
                    finish()            // wracamy do logowania
                }
            }
        }

        /* -------- powrót „Log in” -------- */
        binding.tvGoLogin.setOnClickListener { finish() }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}