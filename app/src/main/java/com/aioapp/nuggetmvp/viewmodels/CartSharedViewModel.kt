package com.aioapp.nuggetmvp.viewmodels

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

    private val _itemList = MutableStateFlow<ArrayList<Food>>(arrayListOf())
    val itemList: StateFlow<ArrayList<Food>> get() = _itemList.asStateFlow()

    fun addItemIntoCart(food: Food) {
        _itemList.value = (_itemList.value + food) as ArrayList<Food>
        state.value = CartProcessingStatus.AddItemIntoCart(food)
    }

}

sealed class CartProcessingStatus {
    data object Init : CartProcessingStatus()
    data class AddItemIntoCart(val food: Food) : CartProcessingStatus()
}
