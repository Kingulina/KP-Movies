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
        items.clear(); items.addAll(list); notifyDataSetChanged()
    }

    inner class VH(val b: RowMovieBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        VH(RowMovieBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, i: Int) = h.apply {
        val m = items[i]
        b.tvTitle.text = m.title
        Glide.with(b.imgPoster).load(m.poster).into(b.imgPoster)
        itemView.setOnClickListener { onClick(m) }
    }
}