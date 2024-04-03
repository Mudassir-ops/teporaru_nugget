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
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.ui.MainActivity
import com.aioapp.nuggetmvp.utils.Constants
import com.aioapp.nuggetmvp.utils.wakeupCallBack
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
        if (Constants.cartItemList?.size!! > 0) binding.headerLayout.tvCartCount.text =
            Constants.cartItemList?.size.toString()
        val drinkList = getDrinksList()
        val foodAdapter = FoodAdapter(requireActivity() as MainActivity, drinkList) {
            val bundle = Bundle()
            bundle.putParcelable("FoodItem", it)
            if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
                findNavController().navigate(
                    R.id.action_drinkMenuFragment_to_itemFullViewFragment, bundle
                )
            }
        }
        binding.rvDrinks.adapter = foodAdapter
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
                    val foodItems = states.value?.mapNotNull { state ->
                        getDrinksList().find { it?.name == state.parametersEntity?.name }
                    }
                    if ((foodItems?.size ?: 0) > 1) {
                        if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
                            foodItems?.forEach { food ->
                                cartSharedViewModel.addItemIntoCart(food)
                            }
                            findNavController().navigate(R.id.action_drinkMenuFragment_to_cartFragment)
                        }
                    } else {
                        if (findNavController().currentDestination?.id == R.id.drinkMenuFragment) {
                            if (foodItems?.get(0) != null) {
                                cartSharedViewModel.addItemIntoCart(foodItems[0])
                            }
                            val bundle = Bundle()
                            bundle.putParcelable(
                                "FoodItem", foodItems?.get(0)
                            )
                            findNavController().navigate(
                                R.id.action_drinkMenuFragment_to_itemFullViewFragment, bundle
                            )
                        }
                    }


                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun getDrinksList(): List<Food?> {
        return listOf(
            Food(R.drawable.pina_colada, R.drawable.pina_colada_full_img, "Pina Colada", "$24"),
            Food(R.drawable.mojito, R.drawable.mojito_full_img, "Mojito", "$24"),
            Food(R.drawable.margarita, R.drawable.margaritta_full_img, "Margarita", "$24"),
            Food(R.drawable.mile_high, R.drawable.mile_high_full_img, "Mile High", "$14"),
            Food(R.drawable.coke, R.drawable.coke_full_img, "Coke", "$14"),
            Food(R.drawable.maverick, R.drawable.mavrick_full_img, "Maverick", "$14"),
            Food(R.drawable.wingman, R.drawable.wingman_full_img, "Wingman", "$14"),
//            Food(
//                R.drawable.martini,
//                R.drawable.martini_full_img,
//                "Martini","$14"
//            ),
            Food(R.drawable.iceman, R.drawable.iceman_full_img, "Iceman", "$14")
        )
    }
}