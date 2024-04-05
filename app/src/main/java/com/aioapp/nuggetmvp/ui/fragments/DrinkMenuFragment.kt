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
import com.aioapp.nuggetmvp.databinding.FragmentDrinkMenuBinding
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
class DrinkMenuFragment : Fragment() {

    private lateinit var binding: FragmentDrinkMenuBinding
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
        // Inflate the layout for this fragment
        binding = FragmentDrinkMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drinkList = getDrinksList()
        val foodAdapter = FoodAdapter(activity ?: return, drinkList) {
            val bundle = Bundle()
            bundle.putParcelable("FoodItem", it)
            if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
                findNavController().navigate(
                    R.id.action_drinkMenuFragment_to_itemFullViewFragment, bundle
                )
            }
        }
        binding.rvDrinks.adapter = foodAdapter
        observeStates()
    }

    private fun observeStates() {
        nuggetSharedViewModel.mState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            handleNuggetProcessingStatus(states)
        }.launchIn(lifecycleScope)
    }

    private fun getDrinksList(): List<Food?> {
        return listOf(
            Food(R.drawable.pina_colada, R.drawable.pina_colada_full_img, "Pina Colada", "24"),
            Food(R.drawable.mojito, R.drawable.mojito_full_img, "Mojito", "24"),
            Food(R.drawable.margarita, R.drawable.margaritta_full_img, "Margarita", "24"),
            Food(R.drawable.mile_high, R.drawable.mile_high_full_img, "Mile High", "14"),
            Food(R.drawable.coke, R.drawable.coke_full_img, "Coke", "14"),
            Food(R.drawable.maverick, R.drawable.mavrick_full_img, "Maverick", "14"),
            Food(R.drawable.wingman, R.drawable.wingman_full_img, "Wingman", "14"),
            Food(R.drawable.martini, R.drawable.martini_full_img, "Martini", "14"),
            Food(R.drawable.iceman, R.drawable.iceman_full_img, "Iceman", "14")
        )
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
        if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
            foodItems.forEach { food ->
                Log.e("FoodItem--->", "handleMultipleItemsState: $food")
                cartSharedViewModel.addItemIntoCart(food)
            }
            findNavController().navigate(R.id.action_drinkMenuFragment_to_cartFragment)
        }
    }


    private fun handleShowMenuIntent(parametersEntity: ParametersEntity?) {
        when (parametersEntity?.menuType) {
            MenuType.FOOD.name.lowercase() -> {
                if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
                    findNavController().navigate(R.id.action_drinkMenuFragment_to_foodMenuFragment)
                }
            }

            MenuType.DRINKS.name.lowercase() -> {
                if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
                    return
                }
            }

            else -> {
                if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
                    findNavController().navigate(R.id.action_drinkMenuFragment_to_foodMenuFragment)
                }
            }
        }
    }

    private fun handleSingleItemState(foodItem: Food) {
        if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {

            cartSharedViewModel.addItemIntoCart(foodItem)
            val bundle = Bundle().apply {
                putParcelable("FoodItem", foodItem)
            }
            findNavController().navigate(
                R.id.action_drinkMenuFragment_to_itemFullViewFragment, bundle
            )
        }
    }
}