package com.example.kpmovies.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kpmovies.data.user.UserEntity
import com.example.kpmovies.databinding.ItemUserBinding

class UserAdapter(
    private val onClick: (UserEntity) -> Unit
) : ListAdapter<UserEntity, UserAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(a: UserEntity, b: UserEntity) = a.login == b.login
        override fun areContentsTheSame(a: UserEntity, b: UserEntity) = a == b
    }

    inner class VH(val vb: ItemUserBinding) : RecyclerView.ViewHolder(vb.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = getItem(position)
        holder.vb.tvLogin.text = user.login
        holder.itemView.setOnClickListener { onClick(user) }
    }
}
