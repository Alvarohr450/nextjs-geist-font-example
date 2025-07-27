package com.example.focuslock.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focuslock.data.models.Filter
import com.example.focuslock.databinding.ItemFilterBinding

class FilterAdapter(
    private val onFilterClick: (Filter) -> Unit
) : ListAdapter<Filter, FilterAdapter.FilterViewHolder>(FilterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = ItemFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FilterViewHolder(
        private val binding: ItemFilterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(filter: Filter) {
            binding.filterName.text = filter.displayName
            
            // Set selection state
            binding.root.isSelected = filter.isSelected
            
            // Set preview background color based on filter
            binding.filterPreview.setBackgroundColor(getFilterPreviewColor(filter.name))
            
            // Set click listener
            binding.root.setOnClickListener {
                onFilterClick(filter)
            }
        }
        
        private fun getFilterPreviewColor(filterName: String): Int {
            return when (filterName.lowercase()) {
                "vintage" -> 0xFFD2B48C.toInt() // Tan
                "dramatic" -> 0xFF2F2F2F.toInt() // Dark gray
                "bright" -> 0xFFFFFACD.toInt() // Light yellow
                "warm" -> 0xFFFFB347.toInt() // Orange
                "cool" -> 0xFF87CEEB.toInt() // Sky blue
                "sepia" -> 0xFFF4A460.toInt() // Sandy brown
                "black_white" -> 0xFF808080.toInt() // Gray
                "vivid" -> 0xFFFF69B4.toInt() // Hot pink
                "soft" -> 0xFFF0F8FF.toInt() // Alice blue
                else -> 0xFF666666.toInt() // Default gray
            }
        }
    }

    class FilterDiffCallback : DiffUtil.ItemCallback<Filter>() {
        override fun areItemsTheSame(oldItem: Filter, newItem: Filter): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Filter, newItem: Filter): Boolean {
            return oldItem == newItem
        }
    }
}
