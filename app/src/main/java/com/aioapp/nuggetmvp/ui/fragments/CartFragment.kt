package com.aioapp.nuggetmvp.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.adapters.CartAdapter
import com.aioapp.nuggetmvp.databinding.FragmentCartBinding
import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.models.ParametersEntity
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.utils.appextension.showToast
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.utils.enum.MenuType
import com.aioapp.nuggetmvp.utils.wakeupCallBack
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private var cartAdapter: CartAdapter? = null
    private var isFirstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartAdapter = CartAdapter(context = context ?: return, cartItemList = arrayListOf())
        wakeupCallBack = {
            context?.let { it1 ->
                ContextCompat.startForegroundService(
                    it1, Intent(it1, NuggetRecorderService::class.java)
                )
            }
            nuggetSharedViewModel.setRecordingStarted()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCartItems()
        observeState()
        setUpAdapter()
    }

    private fun setPrices() {
        val totalPrice = calculatePrice()
        if (totalPrice != 0.0) {
            val taxAmount = 0.1 * totalPrice
            binding?.tvTotalPrice?.text =
                "$".plus(String.format("%.2f", totalPrice.plus(taxAmount)))
            binding?.tvTaxAmount?.text =
                getString(R.string.tax).plus(String.format("%.2f", taxAmount))
        }
    }

    private fun observeCartItems() {
        cartSharedViewModel.itemList.observe(viewLifecycleOwner) { cartItemList ->
            var totalCartItemCount = 0
            cartItemList.forEach { item ->
                totalCartItemCount += item.count
            }
            Log.e("Observer_Remove--->", "observeState:$cartItemList ")
            binding?.headerLayout?.tvCartCount?.text = totalCartItemCount.toString()
            cartAdapter?.updateCartItem(cartItemList = cartItemList)
            setPrices()
            if (cartItemList.isEmpty()) {
                if (findNavController().currentDestination?.id == R.id.cartFragment) {
                    findNavController().navigate(R.id.action_cartFragment_to_foodMenuFragment)
                }
            } else {
                binding?.tvBottomPrompt?.text = getString(R.string.say_nugget_confirm_my_order)
            }
        }
    }

    private fun observeState() {
        nuggetSharedViewModel.mState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            when (states) {
                NuggetProcessingStatus.Init -> Log.e("NuggetMvp", "onViewCreated: Init")

                is NuggetProcessingStatus.RecordingStarted -> binding?.tvBottomPrompt?.text =
                    getString(R.string.listening)

                is NuggetProcessingStatus.RecordingEnded -> Log.e(
                    "NuggetMvp", "onViewCreated: Init${states.isEnded}"
                )

                is NuggetProcessingStatus.TranscriptStarted -> binding?.tvBottomPrompt?.text =
                    getString(R.string.transcripitng)

                is NuggetProcessingStatus.TranscriptEnd -> {
                    binding?.tvBottomPrompt?.text = states.value
                }

                is NuggetProcessingStatus.TextToResponseEnded -> {
                    handleTextToResponseEndedState(states)
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun setUpAdapter() {
        binding?.rvCartItems?.run {
            adapter = cartAdapter
            hasFixedSize()
        }
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        states.value?.forEach { state ->
            Log.e("HiNugget--->", "observeState:$state ")
            when (state.intent) {
                IntentTypes.ADD.label -> {
                    handleAddIntoCartIntent(state.parametersEntity)
                }

                IntentTypes.REMOVE.label -> {
                    handleRemoveFromCartIntent(state.parametersEntity)
                }

                IntentTypes.PLACE_ORDER.label -> {
                    // TODO("Check In Case oF Confirm Order Intent Navigate")
                    if (findNavController().currentDestination?.id == R.id.cartFragment) {
                        findNavController().navigate(R.id.action_cartFragment_to_orderConfirmationFragment)
                    }
                }

                IntentTypes.SHOW_MENU.label -> {
                    handleShowMenuIntent(state.parametersEntity)
                }
            }
        }

    }

    private fun handleShowMenuIntent(parametersEntity: ParametersEntity?) {
        when (parametersEntity?.menuType) {
            MenuType.FOOD.name.lowercase() -> {
                if (findNavController().currentDestination?.id == R.id.cartFragment) {
                    findNavController().navigate(R.id.action_cartFragment_to_foodMenuFragment)
                }
            }

            MenuType.DRINKS.name.lowercase() -> {
                if (findNavController().currentDestination?.id == R.id.cartFragment) {
                    findNavController().navigate(R.id.action_cartFragment_to_drinkMenuFragment)
                }
            }

            else -> {
                if (findNavController().currentDestination?.id == R.id.cartFragment) {
                    findNavController().navigate(R.id.action_cartFragment_to_foodMenuFragment)
                }
            }
        }
    }

    private fun handleRemoveFromCartIntent(parametersEntity: ParametersEntity?) {
        Log.e("remove Item--->", "item:$parametersEntity ")
        val allMenuItems: List<Food?> = nuggetSharedViewModel.allMenuItemsResponse.value
        val cartItem = allMenuItems.find { it?.logicalName == parametersEntity?.name }?.apply {
            val newQuantity = parametersEntity?.quantity ?: 0
            this@apply.itemQuantity = newQuantity
        }
        if (cartItem != null) {
            cartSharedViewModel.removeItemFromCart(cartItem)
        } else {
            context?.showToast("Item Not Available")
        }
        Log.e("remove Item--->", "item:$cartItem")
    }

    private fun handleAddIntoCartIntent(parametersEntity: ParametersEntity?) {
        val allMenuItems: List<Food?> = nuggetSharedViewModel.allMenuItemsResponse.value
        val cartItem = allMenuItems.find { it?.logicalName == parametersEntity?.name }?.apply {
            val newQuantity = parametersEntity?.quantity ?: 0
            this@apply.itemQuantity = newQuantity
        }
        if (cartItem != null) {
            cartSharedViewModel.addItemIntoCart(cartItem)
        } else {
            context?.showToast("Item Not Available")
        }
    }

    private fun calculatePrice(): Double {
        var amount = 0.0
        if (!cartSharedViewModel.itemList.value.isNullOrEmpty()) {
            cartSharedViewModel.itemList.value?.let { list ->
                for (item in list) {
                    amount += if (item.count != 0) {
                        (item.price?.toInt() ?: 0) * (item.count)
                    } else {
                        item.price?.toInt() ?: 0
                    }
                }
            }
        }
        return amount
    }
}