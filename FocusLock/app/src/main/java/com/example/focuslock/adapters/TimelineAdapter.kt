package com.example.focuslock.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focuslock.data.models.VideoClip
import com.example.focuslock.databinding.ItemTimelineClipBinding

class TimelineAdapter(
    private val onClipClick: (VideoClip) -> Unit
) : ListAdapter<VideoClip, TimelineAdapter.TimelineViewHolder>(TimelineDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineClipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TimelineViewHolder(
        private val binding: ItemTimelineClipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clip: VideoClip) {
            binding.clipName.text = clip.name
            binding.clipDuration.text = formatDuration(clip.duration)
            
            // Set selection state
            binding.root.isSelected = clip.isSelected
            
            // Set click listener
            binding.root.setOnClickListener {
                onClipClick(clip)
            }
            
            // Set width based on duration (minimum 80dp, scale with duration)
            val minWidth = 80 * binding.root.context.resources.displayMetrics.density
            val scaledWidth = (minWidth + (clip.duration * 10)).toInt()
            
            val layoutParams = binding.root.layoutParams
            layoutParams.width = scaledWidth
            binding.root.layoutParams = layoutParams
        }
        
        private fun formatDuration(duration: Float): String {
            val minutes = (duration / 60).toInt()
            val seconds = (duration % 60).toInt()
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    class TimelineDiffCallback : DiffUtil.ItemCallback<VideoClip>() {
        override fun areItemsTheSame(oldItem: VideoClip, newItem: VideoClip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoClip, newItem: VideoClip): Boolean {
            return oldItem == newItem
        }
    }
}
