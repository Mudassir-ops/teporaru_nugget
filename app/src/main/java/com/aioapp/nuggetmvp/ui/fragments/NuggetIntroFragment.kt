package com.aioapp.nuggetmvp.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.utils.appextension.colorizeWordInSentence
import com.aioapp.nuggetmvp.utils.appextension.handleNoneState
import com.aioapp.nuggetmvp.utils.enum.MenuType
import com.aioapp.nuggetmvp.viewmodels.NuggetMainViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class NuggetIntroFragment : Fragment() {
    private var binding: FragmentNuggetIntroBinding? = null
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private val mediaPlayer = MediaPlayer()

    private val nuggetMainViewModel: NuggetMainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNuggetIntroBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNonStates()
        binding?.introAnimationView?.playAnimation()
        wakeUpSound()
        mediaPlayer.start()
        binding?.introAnimationView?.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.tvBottomPrompt?.visibility = View.VISIBLE
            binding?.tvBottomPrompt?.text = getString(R.string.try_show_me_the_drinks_menu).colorizeWordInSentence("drinks menu")
            setAnimationOnTextView()
            if (mediaPlayer.isPlaying)
                mediaPlayer.stop()
            mediaPlayer.release()
        }, 1000)
        nuggetSharedViewModel.mState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            handleState(states)
        }.launchIn(lifecycleScope)
    }

    private fun wakeUpSound() {
        val soundFile = resources.openRawResourceFd(R.raw.nugget_wake_up)
        mediaPlayer.setDataSource(soundFile.fileDescriptor, soundFile.startOffset, soundFile.length)
        mediaPlayer.prepare()
    }

    private fun handleState(states: NuggetProcessingStatus) {
        when (states) {
            is NuggetProcessingStatus.Init -> handleInitState()
            is NuggetProcessingStatus.RecordingStarted -> handleRecordingStartedState()
            is NuggetProcessingStatus.TranscriptStarted -> handleTranscriptStartedState()
            is NuggetProcessingStatus.ParitialTranscriptionState -> handleTranscriptEndState(states.value)
            is NuggetProcessingStatus.TextToResponseEnded -> handleTextToResponseEndedState(states.value)
            is NuggetProcessingStatus.RecordingEnded -> TODO()
        }
    }

    private fun handleInitState() {
        Log.e("NuggetMvp", "onViewCreated: Init")
    }

    private fun handleRecordingStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
        binding?.tvBottomPrompt?.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
    }


    private fun handleTranscriptStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.transcripitng)
    }

    private fun handleTranscriptEndState(value: String?) {
        binding?.tvBottomPrompt?.text = value
    }

    private fun handleTextToResponseEndedState(value: TextToResponseIntent?) {
        value?.let {
            if (it.last == true) {
                navigateBasedOnMenuType(it.parametersEntity?.menuType)
            }
        }
    }

    private fun navigateBasedOnMenuType(menuType: String?) {
        when (menuType?.lowercase()) {
            MenuType.FOOD.name.lowercase() -> navigateToFoodMenuFragment()
            MenuType.DRINKS.name.lowercase() -> navigateToDrinkMenuFragment()
            else -> navigateToDefaultMenuFragment()
        }
    }

    private fun navigateToFoodMenuFragment() {
        if (findNavController().currentDestination?.id == R.id.nuggetIntroFragment) {
            findNavController().navigate(R.id.action_nuggetIntroFragment_to_foodMenuFragment)
        }
    }

    private fun navigateToDrinkMenuFragment() {
        if (findNavController().currentDestination?.id == R.id.nuggetIntroFragment) {
            findNavController().navigate(R.id.action_nuggetIntroFragment_to_drinkMenuFragment)
        }
    }

    private fun navigateToDefaultMenuFragment() {
        navigateToFoodMenuFragment() // Default navigation
    }

    private fun setAnimationOnTextView() {
        val anim = AlphaAnimation(0f, 1f)
        anim.setDuration(5000)
        anim.setRepeatCount(Animation.INFINITE)
        anim.repeatMode = Animation.REVERSE
        binding?.tvBottomPrompt?.startAnimation(anim)
    }

    private fun observeNonStates() {
        nuggetMainViewModel.itemResponseStates.observe(viewLifecycleOwner) { txtToResponse ->
            if (txtToResponse?.isNotEmpty() == true) {
                val myData: TextToResponseIntent =
                    Gson().fromJson(txtToResponse, TextToResponseIntent::class.java)
                if (myData.intent?.contains("none", ignoreCase = true) == true) {
                    binding?.tvBottomPrompt?.handleNoneState(context ?: return@observe)
                }
            }
        }
    }

}