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
import com.aioapp.nuggetmvp.databinding.FragmentRefillBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.service.NuggetCameraService
import com.aioapp.nuggetmvp.utils.appextension.colorizeWordInSentence
import com.aioapp.nuggetmvp.utils.appextension.handleNoneState
import com.aioapp.nuggetmvp.utils.appextension.isServiceRunning
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetMainViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class RefillFragment : Fragment() {

    private var binding: FragmentRefillBinding? = null
    private val nuggetMainViewModel: NuggetMainViewModel by activityViewModels()
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private var isFirstTime = true
    private var isApiCalled = false
    private val mediaPlayer = MediaPlayer()
    private var isUserListening = false

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
        setInterChangeableText()
        initiateConvoSound()
        mediaPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying)
                mediaPlayer.stop()
            mediaPlayer.release()
        }, 1000)

        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isApiCalled) {
                if (findNavController().currentDestination?.id == R.id.refillFragment) {
                    findNavController().navigate(
                        R.id.action_refillFragment_to_desertCarouselFragment
                    )
                }
            }
        }, 30000)
        observeState()
        observeNoneState()
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
                    isApiCalled = true
                    handleTextToResponseEndedState(states)
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleTextToResponseEndedState(states: NuggetProcessingStatus.TextToResponseEnded) {
        stopBottomEyeAnim()
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
                navigateToPaymentAfter30Sec()
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
                navigateToPaymentAfter30Sec()
            }
        }
    }

    private fun initiateConvoSound() {
        val soundFile = resources.openRawResourceFd(R.raw.nugget_nitiating_conversation)
        mediaPlayer.setDataSource(soundFile.fileDescriptor, soundFile.startOffset, soundFile.length)
        mediaPlayer.prepare()
    }

    private fun handleRecordingStartedState() {
        isUserListening = true
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
        binding?.tvBottomPrompt?.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
        binding?.bottomEyeAnim?.playAnimation()
    }

    private fun navigateToPaymentAfter30Sec() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isApiCalled) {
                if (findNavController().currentDestination?.id == R.id.refillFragment) {
                    findNavController().navigate(
                        R.id.action_refillFragment_to_desertCarouselFragment
                    )
                }
            }
        }, 30000)
    }

    private fun observeNoneState() {
        nuggetMainViewModel.itemResponseStates.observe(viewLifecycleOwner) { txtToResponse ->
            if (txtToResponse?.isNotEmpty() == true) {
                val myData: TextToResponseIntent =
                    Gson().fromJson(txtToResponse, TextToResponseIntent::class.java)
                if (myData.intent?.contains("none", ignoreCase = true) == true) {
                    binding?.tvBottomPrompt?.handleNoneState(context ?: return@observe)
                    stopBottomEyeAnim()
//                    isApiCalled = false
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
        val baseString = "Say “Nugget, %s my drink”"
        val anim = AlphaAnimation(0f, 1f).apply {
            duration = 4000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        val textFlow = flow {
            while (true) {
                if (!isUserListening) {
                    stopBottomEyeAnim()
                    emit(baseString.format("refill").colorizeWordInSentence("refill"))
                    delay(8000)
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

    private fun stopBottomEyeAnim() {
        binding?.bottomEyeAnim?.cancelAnimation()
        binding?.bottomEyeAnim?.progress = 0F
        binding?.bottomEyeAnim?.setAnimation(R.raw.eye_blinking)

    }
}