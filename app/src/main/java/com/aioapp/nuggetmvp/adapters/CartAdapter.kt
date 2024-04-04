package com.aioapp.nuggetmvp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.CartItemBinding
import com.aioapp.nuggetmvp.models.Food

class CartAdapter(
    private val context: Context, private var cartItemList: List<Food?>
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateCartItem(cartItemList: List<Food?>) {
        cartItemList.also { this.cartItemList = it }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItemList[position]
        holder.bind(cartItem!!)
    }

    inner class ViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cartItem: Food) {
            binding.ivItem.setImageDrawable(
                ContextCompat.getDrawable(
                    context, cartItem.image
                )
            )
            binding.tvItemName.text = String.format(
                context.getString(R.string.cart_item_format), cartItem.count, cartItem.logicalName
            )
            binding.tvPrice.text = "$".plus(cartItem.price)
        }
    }
}