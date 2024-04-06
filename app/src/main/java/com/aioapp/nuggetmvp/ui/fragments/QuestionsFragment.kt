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
import com.aioapp.nuggetmvp.databinding.FragmentQuestionsBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.service.NuggetCameraService
import com.aioapp.nuggetmvp.utils.appextension.handleNoneState
import com.aioapp.nuggetmvp.utils.appextension.isServiceRunning
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.utils.imageSavedToGalleryCallBack
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetMainViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class QuestionsFragment : Fragment() {
    private var binding: FragmentQuestionsBinding? = null
    private val nuggetMainViewModel: NuggetMainViewModel by activityViewModels()
    private val nuggetSharedViewModel: NuggetSharedViewModel by activityViewModels()
    private val cartSharedViewModel: CartSharedViewModel by activityViewModels()
    private var isFirstTime = true
    private var requiredIem: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextCompat.startForegroundService(
            context ?: return, Intent(context ?: return, NuggetCameraService::class.java)
        )
        imageSavedToGalleryCallBack = {
            val file = File(it)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
            nuggetMainViewModel.refill(
                image = imagePart
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.questionAnimation?.playAnimation()
        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        binding?.bottomEyeAnim?.playAnimation()
        observeState()
        observeNoneState()
        observeRefillResponse()
    }

    private fun handleRecordingStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
        binding?.tvBottomPrompt?.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
    }

    private fun observeRefillResponse() {
        Log.e("ObserverWorkingHere--->", "observeRefillResponse: $")
        nuggetMainViewModel.refillResponse.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            when {
                states?.prediction?.lowercase() == "Refill".lowercase() -> {
                    if (findNavController().currentDestination?.id == R.id.questionsFragment) {
                        context?.stopService(
                            Intent(
                                context ?: return@onEach, NuggetCameraService::class.java
                            )
                        )
                        if (isAdded && !isDetached) {
                            findNavController().navigate(R.id.action_questionsFragment_to_refillFragment)
                        }
                    }
                }
            }
        }.launchIn(lifecycleScope)
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
                    binding?.tvBottomPrompt?.setTextColor(
                        ContextCompat.getColor(
                            context ?: return@onEach, R.color.white
                        )
                    )
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
            IntentTypes.NEEDS_EXTRA.label -> {
                requiredIem = states.value.parametersEntity?.requiredThing
                if (binding?.viewFlipper?.displayedChild == 0) {
                    binding?.viewFlipper?.showNext()
                    binding?.tvBottomText?.text =
                        getString(R.string.sure_your).plus(" ").plus(requiredIem)
                            .plus(" will be here shortly")
                    binding?.bottomEyeAnim2?.playAnimation()
                } else {
                    binding?.tvBottomText?.text =
                        getString(R.string.sure_your).plus(" ").plus(requiredIem)
                            .plus(" will be here shortly")
                }
                if (context?.isServiceRunning(NuggetCameraService::class.java) != true) {
                    ContextCompat.startForegroundService(
                        context ?: return,
                        Intent(context ?: return, NuggetCameraService::class.java)
                    )
                }
            }

            IntentTypes.PAYMENT.label -> {
                if (findNavController().currentDestination?.id == R.id.questionsFragment) {
                    context?.stopService(Intent(context ?: return, NuggetCameraService::class.java))
                    findNavController().navigate(R.id.action_questionsFragment_to_desertCarouselFragment)
                }

            }
        }
    }

    private fun observeNoneState() {
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