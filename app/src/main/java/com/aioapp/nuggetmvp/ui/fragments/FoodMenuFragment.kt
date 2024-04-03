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
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.utils.Constants
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
        if (Constants.cartItemList?.size!! > 0) binding.headerLayout.tvCartCount.text =
            Constants.cartItemList?.size.toString()
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

        nuggetSharedViewModel.mState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            when (states) {
                NuggetProcessingStatus.Init -> Log.e("NuggetMvp", "onViewCreated: Init")

                is NuggetProcessingStatus.RecordingStarted -> binding.tvBottomPrompt.text =
                    getString(R.string.listening)

                is NuggetProcessingStatus.RecordingEnded -> Log.e(
                    "NuggetMvp", "onViewCreated: Init${states.isEnded}"
                )

                is NuggetProcessingStatus.TranscriptStarted -> binding.tvBottomPrompt.text =
                    getString(R.string.transcripitng)

                is NuggetProcessingStatus.TranscriptEnd -> binding.tvBottomPrompt.text =
                    states.value

                is NuggetProcessingStatus.TextToResponseEnded -> {
                    if (isFirstTime) {
                        isFirstTime = false
                        return@onEach
                    }
                    if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                        val foodItem =
                            getFoodList().find { it?.name == states.value?.get(0)?.parametersEntity?.name }
                        val bundle = Bundle()
                        bundle.putParcelable("FoodItem", foodItem)
                        if (findNavController().currentDestination?.id == R.id.foodMenuFragment) {
                            if (foodItem != null) {
                                cartSharedViewModel.addItemIntoCart(foodItem)
                            }
                            findNavController().navigate(
                                R.id.action_foodMenuFragment_to_itemFullViewFragment, bundle
                            )
                        }
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun getFoodList(): List<Food?> {
        return listOf(
            Food(R.drawable.caesar, R.drawable.ceasar_full_img, "Caesar", "$12"),
            Food(R.drawable.wedge, R.drawable.wedge_full_img, "Wedge", "$14"),
            Food(R.drawable.caprese, R.drawable.caprese_full_img, "Caprese", "$14"),
            Food(R.drawable.pork, R.drawable.pork_full_img, "Pork", "$18"),
            Food(R.drawable.fish, R.drawable.fish_full_img, "Fish", "$18"),
            Food(R.drawable.beef, R.drawable.beef_full_img, "Beef", "$18"),
            //Food(R.drawable.salmon, R.drawable.salmon_full_img, "Salmon", "$28"),
            Food(R.drawable.steak, R.drawable.steak_full_img, "Steak", "$35"),
            Food(R.drawable.chicken, R.drawable.chicken_full_img, "Chicken", "$25")
        )
    }
}