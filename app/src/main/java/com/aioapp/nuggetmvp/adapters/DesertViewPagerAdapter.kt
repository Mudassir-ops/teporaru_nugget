package com.aioapp.nuggetmvp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aioapp.nuggetmvp.databinding.ItemImageBinding

class DesertViewPagerAdapter(private val context: Context,private val images: IntArray) :
    RecyclerView.Adapter<DesertViewPagerAdapter.ImageViewHolder>() {
    private lateinit var binding: ItemImageBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageResId: Int) {
            binding.ivDesert.setImageDrawable(ContextCompat.getDrawable(context,imageResId))
        }
    }
}