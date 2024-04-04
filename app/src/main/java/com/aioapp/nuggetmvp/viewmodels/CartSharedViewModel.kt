package com.aioapp.nuggetmvp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aioapp.nuggetmvp.models.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CartSharedViewModel @Inject constructor(
) : ViewModel() {
    private val state = MutableStateFlow<CartProcessingStatus>(CartProcessingStatus.Init)
    val mState: StateFlow<CartProcessingStatus> get() = state

    private val cartItemList = ArrayList<Food>()


    private val _itemList = MutableLiveData<ArrayList<Food>>(arrayListOf())
    val itemList: LiveData<ArrayList<Food>> get() = _itemList

    fun addItemIntoCart(food: Food) {
        cartItemList.add(food)
        _itemList.value = cartItemList
        state.value = CartProcessingStatus.AddItemIntoCart(food)
    }

    fun removeItemFromCart(food: Food) {
        cartItemList.remove(food)
        _itemList.value = cartItemList
        state.value = CartProcessingStatus.RemoveItemFromCart(food)
    }

}

sealed class CartProcessingStatus {
    data object Init : CartProcessingStatus()
    data class AddItemIntoCart(val food: Food) : CartProcessingStatus()
    data class RemoveItemFromCart(val food: Food) : CartProcessingStatus()
}
