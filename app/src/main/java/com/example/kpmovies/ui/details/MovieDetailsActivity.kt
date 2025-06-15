package com.example.kpmovies.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity      //  ←  DODAJ TO
import com.example.kpmovies.databinding.ActivityMovieDetailsBinding

class MovieDetailsActivity : AppCompatActivity() {

    private val id by lazy { intent.getStringExtra("id")!! }
    private val repo by lazy {
        MovieRepository(RetrofitBuilder.omdb, AppDatabase.get(this).movieDao())
    }
    private val watchDao by lazy { AppDatabase.get(this).watchlistDao() }
    private val revDao by lazy { AppDatabase.get(this).reviewDao() }
    private val me by lazy { SessionManager.getLogin(this) ?: "" }

    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val dto = repo.details(id)
            populateUI(dto)
            refreshAvg(); refreshReviews(); refreshWatchIcon()
        }

        b.btnAddWatch.setOnClickListener {
            lifecycleScope.launch {
                val exists = watchDao.all(me).any { it.movieId == id }
                if (exists) watchDao.remove(me, id)
                else watchDao.add(WatchlistEntity(me, id, "TODO"))
                refreshWatchIcon()
            }
        }

        b.btnSaveReview.setOnClickListener {
            val rating = b.ratingBar.rating.toInt()
            val text = b.etReview.text.toString()
            lifecycleScope.launch {
                revDao.add(ReviewEntity(id, me, rating, text))
                watchDao.add(WatchlistEntity(me, id, "WATCHED"))
                refreshAvg(); refreshReviews(); refreshWatchIcon()
            }
        }
    }

    /* helper-y populateUI, refreshAvg, refreshReviews, refreshWatchIcon
       pomijam dla zwięzłości (ustawiają teksty, adapter recenzji i ikonę)
     */
}
