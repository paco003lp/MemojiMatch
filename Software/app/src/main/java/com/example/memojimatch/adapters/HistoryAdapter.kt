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

class HistoryAdapter : ListAdapter<GameResult, HistoryAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBadge: TextView  = itemView.findViewById(R.id.tvBadge)
        val tvLevel: TextView  = itemView.findViewById(R.id.tvLevel)
        val tvDate: TextView   = itemView.findViewById(R.id.tvDate)
        val tvScore: TextView  = itemView.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = getItem(position)
        val ctx = holder.itemView.context

        val badgeColor = if (result.isWon) R.color.green else R.color.penalty_red
        val scoreColor = if (result.isWon) R.color.green else R.color.penalty_red

        holder.tvBadge.text = ctx.getString(if (result.isWon) R.string.history_badge_win else R.string.history_badge_loss)
        holder.tvBadge.backgroundTintList = ContextCompat.getColorStateList(ctx, badgeColor)

        holder.tvLevel.text  = result.difficulty.lowercase().replaceFirstChar { it.uppercase() }
        holder.tvDate.text   = DateFormatter.format(result.playedAt)
        holder.tvScore.text  = result.score.toString()
        holder.tvScore.setTextColor(ContextCompat.getColor(ctx, scoreColor))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GameResult>() {
        override fun areItemsTheSame(a: GameResult, b: GameResult) = a.id == b.id
        override fun areContentsTheSame(a: GameResult, b: GameResult) = a == b
    }
}
