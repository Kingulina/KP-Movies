package com.example.kpmovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kpmovies.databinding.RowRecentBinding

data class RecentItem(
    val movieId:    String,
    val posterUrl:  String,
    val movieTitle: String,
    val author:     String,
    val rating:     Int
)

class RecentAdapter(
    private val onClick: (RecentItem) -> Unit
) : RecyclerView.Adapter<RecentAdapter.VH>() {

    private val items = mutableListOf<RecentItem>()

    fun submitList(list: List<RecentItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: RowRecentBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RowRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder.b) {
            tvTitle.text   = item.movieTitle
            tvUser.text  = item.author
            ratingBar.rating  = item.rating.toFloat()
            Glide.with(imgPoster).load(item.posterUrl).into(imgPoster)
        }
        holder.itemView.setOnClickListener { onClick(item) }
    }
}
