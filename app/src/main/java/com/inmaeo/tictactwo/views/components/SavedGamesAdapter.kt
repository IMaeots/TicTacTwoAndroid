package com.inmaeo.tictactwo.views.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inmaeo.tictactwo.databinding.ItemSavedGameBinding

class SavedGamesAdapter(
    private val gameNames: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SavedGamesAdapter.SavedGameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedGameViewHolder {
        val binding = ItemSavedGameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SavedGameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedGameViewHolder, position: Int) {
        val gameName = gameNames[position]
        holder.bind(gameName)
        holder.itemView.setOnClickListener {
            onItemClick(gameName)
        }
    }

    override fun getItemCount(): Int = gameNames.size

    class SavedGameViewHolder(private val binding: ItemSavedGameBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(gameName: String) {
            binding.gameNameTextView.text = gameName
        }
    }
}
