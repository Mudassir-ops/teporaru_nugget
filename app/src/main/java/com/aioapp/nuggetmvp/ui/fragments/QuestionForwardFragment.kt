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
import com.aioapp.nuggetmvp.databinding.FragmentQuestionForwardBinding
import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.utils.wakeupCallBack
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class QuestionForwardFragment : Fragment() {

    private var binding: FragmentQuestionForwardBinding? = null
    private var requiredItem: String? = null
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionForwardBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            requiredItem = this.arguments?.getString("RequiredItem") as String
            binding?.tvBottomPrompt?.text =
                getString(R.string.sure_your).plus(" ").plus(requiredItem)
                    .plus(" will be here shortly")
            observeStates()
        }
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
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
    }

    private fun handleRecordingEndedState(states: NuggetProcessingStatus.RecordingEnded) {
        Log.e("NuggetMvp", "onViewCreated: Init${states.isEnded}")
    }

    private fun handleTranscriptStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.transcripitng)
    }

    private fun handleTranscriptEndState(states: NuggetProcessingStatus.TranscriptEnd) {
        binding?.tvBottomPrompt?.text = states.value
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        Log.wtf("States--->Here", "handleTextToResponseEndedState: $states")
        if (findNavController().currentDestination?.id == R.id.questionForwardFragment) {
            findNavController().navigate(
                R.id.action_questionForwardFragment_to_paymentFragment
            )
        }
        //---For now we are just navigating to payment Screen

        /*   val foodItems = states.value?.mapNotNull { state ->
               nuggetSharedViewModel.allMenuItemList.find { it.logicalName == state.parametersEntity?.name }
           }
           if (foodItems?.isNotEmpty() == true) {
               if (foodItems.size > 1) {
                   handleMultipleItemsState(foodItems)
               } else {
                   handleSingleItemState(foodItems[0])
               }
           } else {
               context?.showToast("Item Not Available")
           }*/
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