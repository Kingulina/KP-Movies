package com.example.kpmovies.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kpmovies.R
import com.example.kpmovies.databinding.RowFriendRecentBinding


class FriendRecentAdapter(
    private val click : (FriendRecentItem) -> Unit
) : RecyclerView.Adapter<FriendRecentAdapter.VH>() {

    private val items = mutableListOf<FriendRecentItem>()
    fun submitList(newList: List<FriendRecentItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class VH(val b: RowFriendRecentBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RowFriendRecentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder.b) {
            // plakat
            Glide.with(imgPoster)
                .load(item.posterUrl)
                .placeholder(R.drawable.placeholder)
                .into(imgPoster)

            // tytuł + ocena
            tvTitle.text  = item.movieTitle
            tvRating.text = "★ ${item.rating}"

            root.setOnClickListener { click(item) }
        }
    }
}

data class FriendRecentItem(
    val movieId   : String,
    val posterUrl : String,
    val movieTitle: String,
    val rating    : Int
)
