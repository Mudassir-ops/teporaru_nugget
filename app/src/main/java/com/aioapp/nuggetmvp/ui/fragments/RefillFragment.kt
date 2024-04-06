package com.aioapp.nuggetmvp.ui.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.aioapp.nuggetmvp.databinding.FragmentRefillBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.service.NuggetCameraService
import com.aioapp.nuggetmvp.utils.appextension.isServiceRunning
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RefillFragment : Fragment() {

    private var binding: FragmentRefillBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private var isFirstTime = true
    private val mediaPlayer = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRefillBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateConvoSound()
        mediaPlayer.start()
        binding?.refillAnimation?.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying)
                mediaPlayer.stop()
            mediaPlayer.release()
        }, 1000)

        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        Handler(Looper.getMainLooper()).postDelayed({
            if (findNavController().currentDestination?.id == R.id.refillFragment) {
                findNavController().navigate(
                    R.id.action_refillFragment_to_desertCarouselFragment
                )
            }
        }, 10000)
        observeState()
    }

    private fun observeState() {
        nuggetSharedViewModel.mState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            when (states) {
                NuggetProcessingStatus.Init -> Log.e("NuggetMvp", "onViewCreated: Init")

                is NuggetProcessingStatus.RecordingStarted -> handleRecordingStartedState()

                is NuggetProcessingStatus.RecordingEnded -> Log.e(
                    "NuggetMvp", "onViewCreated: Init${states.isEnded}"
                )

                is NuggetProcessingStatus.TranscriptStarted -> binding?.tvBottomPrompt?.text =
                    getString(R.string.transcripitng)

                is NuggetProcessingStatus.ParitialTranscriptionState -> {
                    binding?.tvBottomPrompt?.text = states.value
                }

                is NuggetProcessingStatus.TextToResponseEnded -> {
                    handleTextToResponseEndedState(states)
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        when (states.value?.intent) {
            IntentTypes.REFILL_DRINK.label -> {
                binding?.tvBottomPrompt?.text =
                    getString(R.string.your_drink_will_be_served_at_the_table)
                binding?.tvBottomPrompt?.setTextColor(
                    ContextCompat.getColor(
                        context ?: return,
                        R.color.orange
                    )
                )
            }

            IntentTypes.DENY.label -> {
                binding?.tvBottomPrompt?.text =
                    getString(R.string.enjoy_your_food)
                binding?.tvBottomPrompt?.setTextColor(
                    ContextCompat.getColor(
                        context ?: return,
                        R.color.orange
                    )
                )
            }
        }
    }

    private fun initiateConvoSound() {
        val soundFile = resources.openRawResourceFd(R.raw.nugget_nitiating_conversation)
        mediaPlayer.setDataSource(soundFile.fileDescriptor, soundFile.startOffset, soundFile.length)
        mediaPlayer.prepare()
    }

    private fun handleRecordingStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
        binding?.tvBottomPrompt?.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
    }
}