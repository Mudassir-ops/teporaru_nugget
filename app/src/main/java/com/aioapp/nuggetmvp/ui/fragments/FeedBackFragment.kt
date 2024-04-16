package com.aioapp.nuggetmvp.ui.fragments

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
import com.aioapp.nuggetmvp.databinding.FragmentFeedBackBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.utils.appextension.colorizeWordInSentence
import com.aioapp.nuggetmvp.utils.appextension.handleNoneState
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
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
class FeedBackFragment : Fragment() {

    private var binding: FragmentFeedBackBinding? = null
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private val nuggetMainViewModel: NuggetMainViewModel by activityViewModels()
    private var isFirstTime = true
    private var isUserListening = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBackBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        binding?.headerLayout?.tvCartCount?.visibility = View.GONE
        binding?.headerLayout?.ivCart?.visibility = View.GONE
        setInterChangeableText()
        observeStates()
        observeNonState()
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
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
        binding?.tvBottomPrompt?.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
        binding?.bottomEyeAnim?.playAnimation()
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
        stopBottomEyeAnim()
        if (isFirstTime) {
            isFirstTime = false
            return
        }
        when (states.value?.intent) {
            IntentTypes.RATING.label -> {
                val bundle = Bundle()
                bundle.putParcelable("Parameters", states.value.parametersEntity)
                if (findNavController().currentDestination?.id == R.id.feedBackFragment) {
                    findNavController().navigate(
                        R.id.action_feedBackFragment_to_feedBackResponseFragment,
                        bundle
                    )
                }
            }

            IntentTypes.INVALID.label -> {
                binding?.tvBottomPrompt?.text = states.value.parametersEntity?.message
            }
        }

        Log.e("FeedBackHere--->", "handleTextToResponseEndedState: $states")
    }

    private fun observeNonState() {
        nuggetMainViewModel.itemResponseStates.observe(viewLifecycleOwner) { txtToResponse ->
            if (txtToResponse?.isNotEmpty() == true) {
                val myData: TextToResponseIntent =
                    Gson().fromJson(txtToResponse, TextToResponseIntent::class.java)
                if (myData.intent?.contains(
                        "none",
                        ignoreCase = true
                    ) == true || myData.intent?.contains("invalid", ignoreCase = true) == true
                ) {
                    binding?.tvBottomPrompt?.handleNoneState(context ?: return@observe)
                    stopBottomEyeAnim()
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
        val baseString = "Try “%s”"
        val anim = AlphaAnimation(0f, 1f).apply {
            duration = 4000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        val textFlow = flow {
            while (true) {
                if (!isUserListening) {
                    stopBottomEyeAnim()
                    emit(baseString.format("Liked it").colorizeWordInSentence("Liked it"))
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