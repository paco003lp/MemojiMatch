package com.example.memojimatch.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memojimatch.databinding.ItemAvatarBinding

class AvatarAdapter(
    private val avatars: List<String>,
    private val onAvatarClick: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var selectedEmoji: String? = null

    fun setSelected(emoji: String?) {
        selectedEmoji = emoji
        notifyDataSetChanged()
    }

    inner class AvatarViewHolder(val binding: ItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val emoji = avatars[position]
        holder.binding.avatarEmoji.text = emoji
        holder.binding.avatarRoot.isSelected = (emoji == selectedEmoji)  // okida selector
        holder.binding.avatarRoot.setOnClickListener { onAvatarClick(emoji) }
    }

    override fun getItemCount() = avatars.size
}