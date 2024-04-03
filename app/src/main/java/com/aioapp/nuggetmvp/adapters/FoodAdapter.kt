package com.aioapp.nuggetmvp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aioapp.nuggetmvp.databinding.FoodItemLayoutBinding
import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.utils.Constants


class FoodAdapter(
    private val context: Context, private val foodList: List<Food?>,
    private val navigateToDetails: (food: Food) -> Unit
) :
    RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    private lateinit var binding: FoodItemLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = FoodItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem!!)
    }

    inner class ViewHolder(val binding: FoodItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {

            binding.roundedImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    food.image
                )
            )
            binding.tvItemName.text = food.name
            binding.clFoodItem.setOnClickListener {
                Constants.cartItemList?.add(food)
                navigateToDetails.invoke(food)
            }
        }
    }
}