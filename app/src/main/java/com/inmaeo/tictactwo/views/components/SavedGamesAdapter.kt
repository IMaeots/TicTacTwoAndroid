package com.inmaeo.tictactwo.views.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inmaeo.tictactwo.R

class SavedGamesAdapter(
    private val gameNames: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SavedGamesAdapter.SavedGameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedGameViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_saved_game, parent, false)
        return SavedGameViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedGameViewHolder, position: Int) {
        val gameName = gameNames[position]
        holder.bind(gameName)
        holder.itemView.setOnClickListener {
            onItemClick(gameName)
        }
    }

    override fun getItemCount(): Int = gameNames.size

    class SavedGameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameNameTextView: TextView = itemView.findViewById(R.id.gameNameTextView)

        fun bind(gameName: String) {
            gameNameTextView.text = gameName
        }
    }
}