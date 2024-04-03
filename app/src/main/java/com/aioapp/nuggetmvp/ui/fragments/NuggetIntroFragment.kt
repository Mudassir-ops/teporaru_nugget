package com.aioapp.nuggetmvp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.FragmentNuggetIntroBinding
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.utils.enum.MenuType
import com.aioapp.nuggetmvp.utils.wakeupCallBack
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class NuggetIntroFragment : Fragment() {

    private lateinit var binding: FragmentNuggetIntroBinding
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
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
        binding = FragmentNuggetIntroBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAnimationOnTextView()
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

                    when (states.value?.get(0)?.parametersEntity?.menuType) {
                        MenuType.FOOD.name.lowercase() -> {
                            if (findNavController().currentDestination?.id == R.id.nuggetIntroFragment) {
                                //  screenStateUpdateCallback?.invoke(ScreenState.FOOD_MENU)
                                findNavController().navigate(R.id.action_nuggetIntroFragment_to_foodMenuFragment)
                            }
                        }

                        MenuType.DRINKS.name.lowercase() -> {
                            if (findNavController().currentDestination?.id == R.id.nuggetIntroFragment) {
                                //  screenStateUpdateCallback?.invoke(ScreenState.DRINKS_MENU)
                                findNavController().navigate(R.id.action_nuggetIntroFragment_to_drinkMenuFragment)
                            }
                        }

                        else -> {
                            "Invalid Response Case"
                        }
                    }
                }
            }
        }.launchIn(lifecycleScope)

    }

    private fun setAnimationOnTextView() {
        val anim = AlphaAnimation(0f, 1f)
        anim.setDuration(3000)
        anim.setRepeatCount(Animation.INFINITE)
        anim.repeatMode = Animation.REVERSE
        binding.tvBottomPrompt.startAnimation(anim)
    }
}