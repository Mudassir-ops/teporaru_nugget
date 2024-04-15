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
import com.aioapp.nuggetmvp.databinding.FragmentQuestionsBinding
import com.aioapp.nuggetmvp.di.datastore.SharedPreferenceUtil
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.service.NuggetCameraService
import com.aioapp.nuggetmvp.utils.actionCallBack
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
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val mediaPlayer = MediaPlayer()
    private var ifApiCallSuccess = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextCompat.startForegroundService(
            context ?: return, Intent(context ?: return, NuggetCameraService::class.java)
        )
        imageSavedToGalleryCallBack = {
            Log.e("NuggetMvpMudassir--->", "Failed to delete previous file$it")
            val file = File(it)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
            nuggetMainViewModel.refill(
                image = imagePart
            )
            lifecycleScope.launch {
                withContext(Main) {
                    delay(100)
                    if (!ifApiCallSuccess) {
                        ifApiCallSuccess = true
                        actionCallBack?.invoke(file.path)
                    }
                }
            }
        }
        /**
         * Timeout ---> Automatically Navigate to CarousalView if not refill response
         **/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateConvoSound()
        mediaPlayer.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
        }, 1000)
        binding?.questionAnimation?.playAnimation()
        binding?.headerLayout?.tvCartCount?.text = SharedPreferenceUtil.savedCartItemsCount
        observeState()
        observeNoneState()
        observeRefillResponse()
        //   navigateToPaymentAfter30Sec()
    }

    private fun navigateToPaymentAfter30Sec() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (findNavController().currentDestination?.id == R.id.questionsFragment) {
                context?.stopService(
                    Intent(
                        context ?: return@postDelayed, NuggetCameraService::class.java
                    )
                )
                findNavController().navigate(R.id.action_questionsFragment_to_desertCarouselFragment)
            }
        }, 40000)
    }

    private fun handleRecordingStartedState() {
        binding?.tvBottomPrompt?.text = getString(R.string.listening)
        binding?.tvBottomPrompt?.setTextColor(
            ContextCompat.getColor(
                context ?: return, R.color.white
            )
        )
        binding?.bottomEyeAnim?.playAnimation()
        if (binding?.viewFlipper?.displayedChild == 1) binding?.bottomEyeAnim2?.playAnimation()
    }

    private fun initiateConvoSound() {
        val soundFile = resources.openRawResourceFd(R.raw.nugget_nitiating_conversation)
        mediaPlayer.setDataSource(soundFile.fileDescriptor, soundFile.startOffset, soundFile.length)
        mediaPlayer.prepare()
    }

    private fun observeRefillResponse() {
        Log.e("ObserverWorkingHere--->", "observeRefillResponse: $")
        nuggetMainViewModel.refillResponse.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { states ->
            Log.e("States--->", "observeRefillResponse: $states")
            ifApiCallSuccess = false
            when (states?.prediction?.lowercase()) {
                "Refill".lowercase() -> {
                    if (findNavController().currentDestination?.id == R.id.questionsFragment) {
//                        context?.stopService(
//                            Intent(
//                                context ?: return@onEach, NuggetCameraService::class.java
//                            )
//                        )
//                        if (isAdded && !isDetached) {
//                            findNavController().navigate(R.id.action_questionsFragment_to_refillFragment)
//                        }
                    }
                }

                else -> {
                    ifApiCallSuccess = true
                    Log.e("States--->", "observeRefillResponse--Else: $states")
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
                /**
                 *Napkins  none case All Right enjoy your food both case refill and Question TODO()
                 **/
                requiredIem = states.value.parametersEntity?.requiredThing
                binding?.servingAnimation?.playAnimation()
                if (binding?.viewFlipper?.displayedChild == 0) {
                    binding?.viewFlipper?.showNext()
                    binding?.tvBottomText?.text =
                        getString(R.string.sure_your).plus(" ").plus(requiredIem)
                            .plus(" will be here shortly")
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