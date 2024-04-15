package com.aioapp.nuggetmvp.ui

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.databinding.ActivityMainBinding
import com.aioapp.nuggetmvp.models.OrderEntity
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import com.aioapp.nuggetmvp.service.recorder.RealtimeTranscriberManager
import com.aioapp.nuggetmvp.utils.Constants
import com.aioapp.nuggetmvp.utils.appextension.showToast
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
            RealtimeTranscriberManager.startTranscription(Constants.assmeblyAiApiKey,
                onSessionStarted = {},
                onPartialTranscript = { partialTranscript ->
                    lifecycleScope.launch {
                        nuggetSharedViewModel.setTranscriptionEnded(partialTranscript)
                    }
                },
                onFinalTranscript = { finalTranscript ->
                    sendTextToIntent(finalTranscript)
                })
        }

        //nagraph ----
    }

    private fun observeTextToResponseStream() {
        viewModel.textToResponseStream.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { txtToResponse ->
            txtToResponse?.let {
                if (txtToResponse != "null") {
                    try {
                        val myData: TextToResponseIntent =
                            Gson().fromJson(txtToResponse, TextToResponseIntent::class.java)
                        nuggetSharedViewModel.setTextToResponseEnded(myData)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    showToast("Api Expired")
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun sendTextToIntent(transcript: String) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val currentDestination = navController.currentDestination

        val orderEntity = OrderEntity(name = "Beef Burger", quantity = 1)
        val orderList = arrayListOf(orderEntity)
        currentDestination?.let { destination ->
            // Get the ID of the current destination
            val destinationId = destination.id
            // You can use destinationId to determine the current destination
            when (destinationId) {
                R.id.feedBackFragment -> {
                    // Handle destination 1
                    val textToResponseRequestBody =
                        TextToResponseRequestBody(
                            userPrompt = transcript,
                            orderEntity = orderList,
                            screenState = 11,
                            orderStatus = 3
                        )
                    viewModel.textToResponse(
                        body = textToResponseRequestBody
                    )
                }

                else -> {
                    val textToResponseRequestBody =
                        TextToResponseRequestBody(
                            userPrompt = transcript,
                            orderEntity = orderList,
                            screenState = 3
                        )
                    viewModel.textToResponse(
                        body = textToResponseRequestBody
                    )
                }
            }
        }

    }
}