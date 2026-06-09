package com.example.memojimatch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memojimatch.R
import com.example.memojimatch.data.GameResult
import com.example.memojimatch.util.DateFormatter

class LeaderboardAdapter(
    private val currentUsername: String,
    private val currentAvatarEmoji: String
) : ListAdapter<GameResult, LeaderboardAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView     = itemView.findViewById(R.id.tvRank)
        val tvAvatar: TextView   = itemView.findViewById(R.id.tvAvatar)
        val tvName: TextView     = itemView.findViewById(R.id.tvName)
        val tvMeta: TextView     = itemView.findViewById(R.id.tvMeta)
        val tvScore: TextView    = itemView.findViewById(R.id.tvScore)
        val tvOwn: TextView      = itemView.findViewById(R.id.tvOwn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = getItem(position)
        val rank = position + 1
        val ctx = holder.itemView.context

        val isFirst = rank == 1
        val isOwn   = result.username.equals(currentUsername, ignoreCase = true)

        val highlightColor = ContextCompat.getColor(ctx, R.color.ms_orange)
        val normalColor    = ContextCompat.getColor(ctx, R.color.white)

        holder.tvRank.text  = rank.toString()
        holder.tvRank.setTextColor(if (isFirst) highlightColor else normalColor)

        holder.tvAvatar.text  = currentAvatarEmoji
        holder.tvName.text    = result.username
        holder.tvScore.text   = result.score.toString()
        holder.tvScore.setTextColor(if (isFirst) highlightColor else normalColor)

        val levelDisplay = result.difficulty.lowercase().replaceFirstChar { it.uppercase() }
        holder.tvMeta.text = ctx.getString(
            R.string.leaderboard_meta,
            levelDisplay,
            DateFormatter.format(result.playedAt)
        )

        holder.tvOwn.visibility = if (isOwn) View.VISIBLE else View.GONE
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GameResult>() {
        override fun areItemsTheSame(a: GameResult, b: GameResult) = a.id == b.id
        override fun areContentsTheSame(a: GameResult, b: GameResult) = a == b
    }
}
