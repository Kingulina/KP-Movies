package com.example.kpmovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kpmovies.databinding.RowMovieBinding
import com.example.kpmovies.data.local.entity.MovieEntity

class MovieAdapter(
    private val onClick: (MovieEntity) -> Unit
) : RecyclerView.Adapter<MovieAdapter.VH>() {

    private val items = mutableListOf<MovieEntity>()

    fun submitList(list: List<MovieEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: RowMovieBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RowMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = items[position]
        with(holder.b) {
            tvTitle.text = m.title
            Glide.with(imgPoster).load(m.poster).into(imgPoster)
        }
        holder.itemView.setOnClickListener { onClick(m) }
    }
}