package com.aioapp.nuggetmvp.ui.fragments

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
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.utils.appextension.colorizeWordInSentence
import com.aioapp.nuggetmvp.utils.appextension.handleNoneState
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.utils.enum.MenuType
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetMainViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodMenuFragment : Fragment() {

    private lateinit var binding: FragmentFoodMenuBinding
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private val nuggetMainViewModel: NuggetMainViewModel by activityViewModels()
    private var isFirstTime = true
    private var isUserListening = false
    private var checkTotalItemCount = 0
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
        setInterChangeableText()


        observeStates()
        observeNonState()
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
            is NuggetProcessingStatus.ParitialTranscriptionState -> handleTranscriptEndState(states)
            is NuggetProcessingStatus.TextToResponseEnded -> handleTextToResponseEndedState(states)
        }
    }

    private fun handleInitState() {
        Log.e("NuggetMvp", "onViewCreated: Init")
    }

    private fun handleRecordingStartedState() {
        isUserListening = true
        binding.tvBottomPrompt.text = getString(R.string.listening)
        binding.tvBottomPrompt.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
        binding.bottomEyeAnim.playAnimation()
    }

    private fun handleRecordingEndedState(states: NuggetProcessingStatus.RecordingEnded) {
        Log.e("NuggetMvp", "onViewCreated: Init${states.isEnded}")
    }

    private fun handleTranscriptStartedState() {
        binding.tvBottomPrompt.text = getString(R.string.transcripitng)
    }

    private fun handleTranscriptEndState(states: NuggetProcessingStatus.ParitialTranscriptionState) {
        binding.tvBottomPrompt.text = states.value
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        stopBottomEyeAnim()
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        Log.e("SHOW_MY_CART", "handleTextToResponseEndedState: ${states.value?.intent}")
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
            if (states.value.last == true) {
                if (cartSharedViewModel.itemList.value?.size!! > 0) {
                    navToCart()
                }
            }
        } else if (states.value?.intent == IntentTypes.SHOW_CART.label) {
            navToCart()
        } else {
            binding.tvBottomPrompt.handleNoneState(context ?: return)
        }
    }

    private fun handleShowMenuIntent(parametersEntity: TextToResponseIntent?) {
        if (parametersEntity?.last == true) {
            when (parametersEntity.parametersEntity?.menuType) {
                MenuType.FOOD.name.lowercase() -> {
                    if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                        isUserListening = false
                        return
                    }
                }

                MenuType.DRINKS.name.lowercase() -> {
                    if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                        findNavController().navigate(R.id.action_foodMenuFragment_to_drinkMenuFragment)
                    }
                }
            }
        }
    }

    private fun navToCart() {
        if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
            findNavController().navigate(R.id.action_foodMenuFragment_to_cartFragment)
        }
    }

    private fun navToSingle(foodItem: Food) {
        if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
            val bundle = Bundle().apply {
                putParcelable("FoodItem", foodItem)
            }
            findNavController().navigate(
                R.id.action_foodMenuFragment_to_itemFullViewFragment, bundle
            )
        }
    }

    private fun observeNonState() {
        nuggetMainViewModel.itemResponseStates.observe(viewLifecycleOwner) { txtToResponse ->
            if (txtToResponse?.isNotEmpty() == true) {
                val myData: TextToResponseIntent =
                    Gson().fromJson(txtToResponse, TextToResponseIntent::class.java)
                if (myData.intent?.contains("none", ignoreCase = true) == true) {
                    binding.tvBottomPrompt.handleNoneState(context ?: return@observe)
                    stopBottomEyeAnim()
                    isUserListening = true
                    lifecycleScope.launch {
                        delay(4000)
                        isUserListening = false
                    }
                }
            }
        }
    }

    private fun setInterChangeableText() {
        val listOfItemName = getFoodList().map { foodItem ->
            foodItem?.logicalName
        }
        val baseString = "“Nugget, Add %s to my order”"
        var currentItemIndex = 0
        val textFlow = flow {
            while (true) {
                if (!isUserListening) {
                    stopBottomEyeAnim()
                    val currentItem = listOfItemName[currentItemIndex]
                    emit(currentItem?.let {
                        baseString.format(currentItem).colorizeWordInSentence(it)
                    })
                    delay(5000)
                    currentItemIndex = (currentItemIndex + 1) % listOfItemName.size
                } else {
                    delay(100)
                }
            }
        }
        lifecycleScope.launch {
            textFlow.collect { text ->
                binding.tvBottomPrompt.text = text
            }
        }
    }

    private fun stopBottomEyeAnim() {
        binding.bottomEyeAnim.cancelAnimation()
        binding.bottomEyeAnim.progress = 0F
        binding.bottomEyeAnim.setAnimation(R.raw.eye_blinking)

    }
}