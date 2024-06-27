package com.aioapp.nuggetmvp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aioapp.nuggetmvp.models.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartSharedViewModel @Inject constructor(
) : ViewModel() {
    private val state = MutableStateFlow<CartProcessingStatus>(CartProcessingStatus.Init)
    val mState: StateFlow<CartProcessingStatus> get() = state
    private var cartItemList = ArrayList<Food>()


    private val _itemList = MutableLiveData<ArrayList<Food>>(arrayListOf())
    val itemList: LiveData<ArrayList<Food>> get() = _itemList

    fun addItemIntoCart(food: Food) {
        val existingItem = cartItemList.find { it.logicalName == food.logicalName }
        if (existingItem != null) {
            existingItem.count += food.itemQuantity
            existingItem.itemQuantity += food.itemQuantity
        } else {
            val foodCopy = food.copy(count = food.itemQuantity, itemQuantity = food.itemQuantity)
            cartItemList.add(foodCopy)
        }
        _itemList.value = ArrayList(cartItemList)
        state.value = CartProcessingStatus.AddItemIntoCart(food)
    }

    fun removeItemFromCart(food: Food) {
        val existingItem = cartItemList.find { it.logicalName == food.logicalName }
        if (existingItem != null) {
            if (existingItem.itemQuantity == 1) {
                cartItemList.remove(existingItem)
            } else {
                existingItem.count -= food.itemQuantity ?: 0
                if(existingItem.count == 0){
                    cartItemList.remove(existingItem)
                }
            }
            _itemList.value = ArrayList(cartItemList)
        }
        state.value = CartProcessingStatus.RemoveItemFromCart(food)
    }
}

sealed class CartProcessingStatus {
    data object Init : CartProcessingStatus()
    data class AddItemIntoCart(val food: Food) : CartProcessingStatus()
    data class RemoveItemFromCart(val food: Food) : CartProcessingStatus()
}
