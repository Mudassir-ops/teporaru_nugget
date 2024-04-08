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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NuggetIntroFragment : Fragment() {
    private var binding: FragmentNuggetIntroBinding? = null
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private val mediaPlayer = MediaPlayer()
    private val nuggetMainViewModel: NuggetMainViewModel by activityViewModels()
    private var isUserListening = false
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
            setInterChangeableText()

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
        isUserListening = true
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
        Log.e("TextToResponseEndedState-->", "handleTextToResponseEndedState: $value")
        value?.let {
            if (it.last == true) {
                if (it.intent != "none") {
                    navigateBasedOnMenuType(it.parametersEntity?.menuType)
                }
            }
        }
    }

    private fun navigateBasedOnMenuType(menuType: String?) {
        when (menuType?.lowercase()) {
            MenuType.DRINKS.name.lowercase() -> navigateToDrinkMenuFragment()
            else -> navigateToFoodMenuFragment()
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

    private fun observeNonStates() {
        nuggetMainViewModel.itemResponseStates.observe(viewLifecycleOwner) { txtToResponse ->
            if (txtToResponse?.isNotEmpty() == true) {
                val myData: TextToResponseIntent =
                    Gson().fromJson(txtToResponse, TextToResponseIntent::class.java)
                if (myData.intent?.contains("none", ignoreCase = true) == true) {
                    binding?.tvBottomPrompt?.handleNoneState(context ?: return@observe)
                    isUserListening = true
                    lifecycleScope.launch {
                        delay(6000)
                        isUserListening = false
                    }
                }
            }
        }
    }

    private fun setInterChangeableText() {
        val baseString = "Try “Nugget, Show me the %s menu”"
        val anim = AlphaAnimation(0f, 1f).apply {
            duration = 4000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        var isDrinksMenu = true
        val textFlow = flow {
            while (true) {
                if (!isUserListening) {
                    val variable = if (isDrinksMenu) "drinks" else "food"
                    emit(baseString.format(variable).colorizeWordInSentence(variable))
                    delay(8000)
                    isDrinksMenu = !isDrinksMenu
                } else {
                    delay(100)
                }
            }
        }
        lifecycleScope.launch {
            textFlow.collect { text ->
                binding?.tvBottomPrompt?.text = text
                binding?.tvBottomPrompt?.startAnimation(anim)
            }
        }
    }
}