package com.aioapp.nuggetmvp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.utils.enum.MenuType
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DrinkMenuFragment : Fragment() {

    private var binding: FragmentDrinkMenuBinding? = null
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private var isFirstTime = true
    private var checkTotalItemCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDrinkMenuBinding.inflate(layoutInflater, container, false)
        return binding?.root
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
        binding?.rvDrinks?.adapter = foodAdapter
        binding?.bottomEyeAnim?.playAnimation()
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
            is NuggetProcessingStatus.ParitialTranscriptionState -> handleTranscriptEndState(states)
            is NuggetProcessingStatus.TextToResponseEnded -> handleTextToResponseEndedState(states)
        }
    }

    private fun handleInitState() {
        Log.e("NuggetMvp", "onViewCreated: Init")
    }

    private fun handleRecordingStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
    }

    private fun handleRecordingEndedState(states: NuggetProcessingStatus.RecordingEnded) {
        Log.e("NuggetMvp", "onViewCreated: Init${states.isEnded}")
    }

    private fun handleTranscriptStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.transcripitng)
    }

    private fun handleTranscriptEndState(states: NuggetProcessingStatus.ParitialTranscriptionState) {
        binding?.tvBottomPrompt?.text = states.value
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        if (states.value?.intent == IntentTypes.SHOW_MENU.label) {
            handleShowMenuIntent(states.value)
        } else if (states.value?.intent == IntentTypes.ADD.label) {
            val allMenuItems: List<Food?> = nuggetSharedViewModel.allMenuItemsResponse.value
            val foodItems =
                allMenuItems.find { it?.logicalName == states.value.parametersEntity?.name }
                    ?.apply {
                        val newQuantity = states.value.parametersEntity?.quantity ?: 0
                        this@apply.itemQuantity = newQuantity
                    }
            if (foodItems != null) {
                checkTotalItemCount++
                if (states.value.last != true) {
                    cartSharedViewModel.addItemIntoCart(foodItems)
                } else {
                    cartSharedViewModel.addItemIntoCart(foodItems)
                    if (checkTotalItemCount > 1) {
                        navToCart()
                    } else {
                        navToSingle(foodItems)
                    }
                }
            }
        }
    }

    private fun navToCart() {
        if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
            findNavController().navigate(R.id.action_drinkMenuFragment_to_cartFragment)
        }
    }

    private fun navToSingle(foodItem: Food) {
        if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
            val bundle = Bundle().apply {
                putParcelable("FoodItem", foodItem)
            }
            findNavController().navigate(
                R.id.action_drinkMenuFragment_to_itemFullViewFragment, bundle
            )
        }
    }

    private fun handleShowMenuIntent(parametersEntity: TextToResponseIntent?) {
        if (parametersEntity?.last == true) {
            when (parametersEntity.parametersEntity?.menuType) {
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
                        findNavController().navigate(R.id.action_nuggetIntroFragment_to_foodMenuFragment)
                    }
                }
            }
        }
    }

}