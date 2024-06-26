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
import com.aioapp.nuggetmvp.service.NuggetCameraService
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.utils.appextension.isServiceRunning
import com.aioapp.nuggetmvp.utils.enum.IntentTypes
import com.aioapp.nuggetmvp.utils.imageSavedToGalleryCallBack
import com.aioapp.nuggetmvp.utils.wakeupCallBack
import com.aioapp.nuggetmvp.viewmodels.CartSharedViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetMainViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetProcessingStatus
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
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
        wakeupCallBack = {
            context?.let { it1 ->
                ContextCompat.startForegroundService(
                    it1, Intent(it1, NuggetRecorderService::class.java)
                )

            }
            nuggetSharedViewModel.setRecordingStarted()
        }
        ContextCompat.startForegroundService(
            context ?: return,
            Intent(context ?: return, NuggetCameraService::class.java)
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.questionAnimation?.playAnimation()
        binding?.headerLayout?.tvCartCount?.text =
            cartSharedViewModel.itemList.value?.size.toString()
        binding?.bottomEyeAnim?.playAnimation()
        observeState()
        observeRefillResponse()
    }

    private fun observeRefillResponse() {
        nuggetMainViewModel.refillResponse.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            if (states?.prediction?.lowercase() == "Refill".lowercase()) {
                if (findNavController().currentDestination?.id == R.id.questionsFragment) {
                    context?.stopService(
                        Intent(
                            context ?: return@onEach,
                            NuggetCameraService::class.java
                        )
                    )
                    if (isAdded && !isDetached) {
                        findNavController().navigate(R.id.action_questionsFragment_to_refillFragment)
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

                is NuggetProcessingStatus.RecordingStarted -> binding?.tvBottomPrompt?.text =
                    getString(R.string.listening)

                is NuggetProcessingStatus.RecordingEnded -> Log.e(
                    "NuggetMvp", "onViewCreated: Init${states.isEnded}"
                )

                is NuggetProcessingStatus.TranscriptStarted -> binding?.tvBottomPrompt?.text =
                    getString(R.string.transcripitng)

                is NuggetProcessingStatus.TranscriptEnd -> {
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
        states.value?.forEach { state ->
            Log.e("HiNugget--->", "observeState:$state ")
            when (state.intent) {
                IntentTypes.NEEDS_EXTRA.label -> {
                    requiredIem = state.parametersEntity?.requiredThing
                    binding?.viewFlipper?.showNext()
                    binding?.tvBottomText?.text =
                        getString(R.string.sure_your).plus(" ").plus(requiredIem)
                            .plus(" will be here shortly")
                      binding?.bottomEyeAnim2?.playAnimation()

                    if (context?.isServiceRunning(NuggetCameraService::class.java) != true) {
                        ContextCompat.startForegroundService(
                            context ?: return,
                            Intent(context ?: return, NuggetCameraService::class.java)
                        )
                    }
                }
            }
        }
    }

}