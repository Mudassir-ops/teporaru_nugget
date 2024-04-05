package com.aioapp.nuggetmvp.ui

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.aioapp.nuggetmvp.databinding.ActivityMainBinding
import com.aioapp.nuggetmvp.models.OrderEntity
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import com.aioapp.nuggetmvp.service.recorder.RealtimeTranscriberManager
import com.aioapp.nuggetmvp.utils.Constants
import com.aioapp.nuggetmvp.utils.wakeupCallBack
import com.aioapp.nuggetmvp.viewmodels.NuggetMainViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val viewModel by viewModels<NuggetMainViewModel>()
    private val nuggetSharedViewModel: NuggetSharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        observeTextToResponseStream()

        wakeupCallBack = {
            nuggetSharedViewModel.setRecordingStarted()
            RealtimeTranscriberManager.startTranscription(
                Constants.assmeblyAiApiKey,
                onSessionStarted = {
                },
                onPartialTranscript = { partialTranscript ->
                    lifecycleScope.launch {
                        nuggetSharedViewModel.setTranscriptionEnded(partialTranscript)
                    }
                },
                onFinalTranscript = { finalTranscript ->
                    sendTextToIntent(finalTranscript)
                })
        }
    }

    private fun observeTextToResponseStream() {
        viewModel.textToResponseStream.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { txtToResponse ->
            if (txtToResponse != null) {
                val myData: TextToResponseIntent =
                    Gson().fromJson(txtToResponse, TextToResponseIntent::class.java)
                nuggetSharedViewModel.setTextToResponseEnded(myData)
            }
        }.launchIn(lifecycleScope)
    }

    private fun sendTextToIntent(transcript: String) {
        val orderEntity = OrderEntity(name = "Beef Burger", quantity = 1)
        val orderList = arrayListOf(orderEntity)
        val textToResponseRequestBody = TextToResponseRequestBody(
            userPrompt = transcript, orderEntity = orderList, screenState = 3
        )
        viewModel.textToResponse(
            body = textToResponseRequestBody
        )
    }
}