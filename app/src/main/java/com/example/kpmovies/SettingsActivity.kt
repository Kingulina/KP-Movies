package com.example.kpmovies

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kpmovies.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Kliknięcia poszczególnych opcji --------------- */
        binding.tvRegulations.setOnClickListener { toast("Regulations – TODO") }
        binding.tvPrivacyPolicy.setOnClickListener { toast("Privacy policy – TODO") }
        binding.tvPrivacySettings.setOnClickListener { toast("Privacy settings – TODO") }

        binding.ivBack.setOnClickListener {
            finish()               // wraca do HomeActivity
        }
        /* Wylogowanie */
        binding.tvLogout.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
