package com.example.kpmovies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kpmovies.databinding.ActivityWatchListBinding

class WatchListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* 1. domyślnie włączamy zakładkę „Watch list” */
        selectTab(isWatchList = true)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // wracamy do HomeActivity
                    finish()            // kończymy bieżące i wracamy
                }
                R.id.nav_search -> {
                    toast("Search – demo")
                }
                R.id.nav_menu -> {
                    toast("Menu – demo")   // tu nie mamy Drawer-a, później ew. otwórz Filter
                }
            }
            true
        }
        binding.bottomNav.menu.findItem(R.id.nav_search).isChecked = true
        /* 2. kliknięcia przełączników */
        binding.btnWatchList.setOnClickListener { selectTab(isWatchList = true) }
        binding.btnWatched.setOnClickListener  { selectTab(isWatchList = false) }

        /* 3. filtr (ikonka lejka) – na razie tylko Toast */
        binding.ivFilter.setOnClickListener {
            Toast.makeText(this, "Filter – TODO", Toast.LENGTH_SHORT).show()
        }

        /* 4. (opcjonalnie) – klik avatar / nickname -> Toast demo */
        binding.ivAvatar.setOnClickListener { toast("Avatar – TODO") }
        binding.tvNickname.setOnClickListener { toast("Profile – TODO") }

        /* 5. wczytaj przykładowe okładki – DEMO */
        loadDummyPosters()
    }

    /* przełącza podświetlenie i (docelowo) zawartość listy */
    private fun selectTab(isWatchList: Boolean) {
        val sel = ContextCompat.getColor(this, R.color.purple_123)
        val not = ContextCompat.getColor(this, android.R.color.darker_gray)

        binding.btnWatchList.setBackgroundColor(if (isWatchList) sel else not)
        binding.btnWatched.setBackgroundColor(if (isWatchList) not else sel)

        /* Tu docelowo odświeżysz adapter RecyclerView. Na razie tylko Toast. */
        toast(if (isWatchList) "Watch list" else "Watched")
    }

    private fun loadDummyPosters() {
        /* Wersja demo: zostawiamy placeholdery w layout-cie,
           więc nie trzeba nic robić. Jeśli użyjesz RecyclerView,
           tu wstawisz adapter-submitList() */
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
