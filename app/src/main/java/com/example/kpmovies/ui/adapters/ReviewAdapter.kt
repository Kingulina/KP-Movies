package com.example.kpmovies.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kpmovies.data.local.entity.ReviewEntity
import com.example.kpmovies.databinding.RowReviewBinding
import java.text.SimpleDateFormat
import java.util.*

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.VH>() {

    private val items = mutableListOf<ReviewEntity>()

    fun submitList(list: List<ReviewEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: RowReviewBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(RowReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = with(holder.b) {
        val review = items[position]

        /* ─── autor + data ─── */
        tvAuthor.text = review.author
        tvDate.text   = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault())
            .format(Date(review.created))

        /* ─── ocena ─── */
        tvRating.text = "★ ${review.rating}"

        /* ─── treść ─── */
        tvText.text = review.text
    }
}