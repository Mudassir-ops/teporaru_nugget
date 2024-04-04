package com.aioapp.nuggetmvp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aioapp.nuggetmvp.databinding.CarouselItemBinding

class ImageAdapter(private val context: Context, private val imageList: List<Int>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder?>() {
    private lateinit var binding: CarouselItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(val binding: CarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Int) {
            binding.listItemImage.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    image
                )
            )
        }
    }
}
