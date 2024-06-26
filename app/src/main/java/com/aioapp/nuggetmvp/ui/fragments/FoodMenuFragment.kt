package com.aioapp.nuggetmvp.ui.fragments

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
import com.aioapp.nuggetmvp.adapters.FoodAdapter
import com.aioapp.nuggetmvp.databinding.FragmentFoodMenuBinding
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
class FoodMenuFragment : Fragment() {

    private lateinit var binding: FragmentFoodMenuBinding
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private var isFirstTime = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    ): View {
        binding = FragmentFoodMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (cartSharedViewModel.itemList.value?.isNotEmpty() == true) binding.headerLayout.tvCartCount.text =
            cartSharedViewModel.itemList.value?.size.toString()
        val foodList = getFoodList()
        val foodAdapter = FoodAdapter(context ?: return, foodList) {
            val bundle = Bundle()
            bundle.putParcelable("FoodItem", it)
            if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                findNavController().navigate(
                    R.id.action_foodMenuFragment_to_itemFullViewFragment, bundle
                )
            }
        }
        binding.rvFood.adapter = foodAdapter
        binding.bottomEyeAnim.playAnimation()
        observeStates()
    }

    private fun getFoodList(): List<Food?> {
        return listOf(
            Food(R.drawable.caesar, R.drawable.ceasar_full_img, "Caesar", "12"),
            Food(R.drawable.wedge, R.drawable.wedge_full_img, "Wedge", "14"),
            Food(R.drawable.caprese, R.drawable.caprese_full_img, "Caprese", "14"),
            Food(R.drawable.pork, R.drawable.pork_full_img, "Pork", "18"),
            Food(R.drawable.fish, R.drawable.fish_full_img, "Fish", "18"),
            Food(R.drawable.beef, R.drawable.beef_full_img, "Beef", "18"),
            Food(R.drawable.salmon, R.drawable.salmon_full_img, "Salmon", "28"),
            Food(R.drawable.steak, R.drawable.steak_full_img, "Steak", "35"),
            Food(R.drawable.chicken, R.drawable.chicken_full_img, "Chicken", "25")
        )
    }

    private fun observeStates() {
        nuggetSharedViewModel.mState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            handleNuggetProcessingStatus(states)
        }.launchIn(lifecycleScope)
    }

    private fun handleNuggetProcessingStatus(states: NuggetProcessingStatus) {
        when (states) {
            NuggetProcessingStatus.Init -> handleInitState()
            is NuggetProcessingStatus.RecordingStarted -> handleRecordingStartedState()
            is NuggetProcessingStatus.RecordingEnded -> handleRecordingEndedState(states)
            is NuggetProcessingStatus.TranscriptStarted -> handleTranscriptStartedState()
            is NuggetProcessingStatus.TranscriptEnd -> handleTranscriptEndState(states)
            is NuggetProcessingStatus.TextToResponseEnded -> handleTextToResponseEndedState(states)
        }
    }

    private fun handleInitState() {
        Log.e("NuggetMvp", "onViewCreated: Init")
    }

    private fun handleRecordingStartedState() {
        binding.tvBottomPrompt.text = getString(R.string.listening)
    }

    private fun handleRecordingEndedState(states: NuggetProcessingStatus.RecordingEnded) {
        Log.e("NuggetMvp", "onViewCreated: Init${states.isEnded}")
    }

    private fun handleTranscriptStartedState() {
        binding.tvBottomPrompt.text = getString(R.string.transcripitng)
    }

    private fun handleTranscriptEndState(states: NuggetProcessingStatus.TranscriptEnd) {
        binding.tvBottomPrompt.text = states.value
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        val state = states.value?.firstOrNull()
        if (state != null) {
            if (state.intent == IntentTypes.ADD.label) {
                addIntentHandling(states)
            } else {
                handleShowMenuIntent(state.parametersEntity)
            }
        }
    }

    private fun handleShowMenuIntent(parametersEntity: ParametersEntity?) {
        when (parametersEntity?.menuType) {
            MenuType.FOOD.name.lowercase() -> {
                if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                    return
                }
            }

            MenuType.DRINKS.name.lowercase() -> {
                if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                    findNavController().navigate(R.id.action_foodMenuFragment_to_drinkMenuFragment)
                }
            }

            else -> {
                if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                    findNavController().navigate(R.id.action_foodMenuFragment_to_drinkMenuFragment)
                }
            }
        }
    }


    private fun addIntentHandling(states: NuggetProcessingStatus.TextToResponseEnded) {
        val allMenuItems: List<Food?> = nuggetSharedViewModel.allMenuItemsResponse.value
        val foodItems = states.value?.mapNotNull { state ->
            allMenuItems.find { it?.logicalName == state.parametersEntity?.name }?.apply {
                val newQuantity = state.parametersEntity?.quantity ?: 0
                this@apply.itemQuantity = newQuantity
            }
        }

        if (foodItems?.isNotEmpty() == true) {
            if (foodItems.size > 1) {
                handleMultipleItemsState(foodItems)
            } else {
                handleSingleItemState(foodItems[0])
            }
        } else {
            context?.showToast("Item Not Available")
        }
    }

    private fun handleMultipleItemsState(foodItems: List<Food>) {
        if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
            foodItems.forEach { food ->
                cartSharedViewModel.addItemIntoCart(food)
            }
            findNavController().navigate(R.id.action_foodMenuFragment_to_cartFragment)
        }
    }

    private fun handleSingleItemState(foodItem: Food) {
        if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
            cartSharedViewModel.addItemIntoCart(foodItem)
            val bundle = Bundle()
            bundle.putParcelable(
                "FoodItem", foodItem
            )
            findNavController().navigate(
                R.id.action_foodMenuFragment_to_itemFullViewFragment, bundle
            )
        }
    }
}