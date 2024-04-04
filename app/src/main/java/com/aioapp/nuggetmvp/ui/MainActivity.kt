package com.aioapp.nuggetmvp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.aioapp.nuggetmvp.databinding.ActivityMainBinding
import com.aioapp.nuggetmvp.models.OrderEntity
import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import com.aioapp.nuggetmvp.service.NuggetRecorderService
import com.aioapp.nuggetmvp.service.helper.Events
import com.aioapp.nuggetmvp.utils.Constants.TAG_NUGET_MVP
import com.aioapp.nuggetmvp.utils.Constants.deepGramlanguage
import com.aioapp.nuggetmvp.utils.Constants.deepGrammodel
import com.aioapp.nuggetmvp.utils.Constants.smartFormat
import com.aioapp.nuggetmvp.viewmodels.AudioTranscriptionState
import com.aioapp.nuggetmvp.viewmodels.NuggetMainViewModel
import com.aioapp.nuggetmvp.viewmodels.NuggetSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val viewModel by viewModels<NuggetMainViewModel>()
    private val nuggetSharedViewModel: NuggetSharedViewModel by viewModels()
    private var bus: EventBus? = null
    private var pauseCounter: Int = 0
    private var isFirstTime = true
    private val initialAmplitudes = mutableListOf<Int>()
    private var threshold = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        bus = EventBus.getDefault()
        bus?.register(this)
        observeDeepGram()
        observeDeepGramDataState()
    }

    private fun observeDeepGramDataState() {
        viewModel.mState.flowWithLifecycle(this@MainActivity.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(this@MainActivity.lifecycleScope)
    }

    private fun observeDeepGram() {
        viewModel.deepGramResponse.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { transcriptionResponse ->
            if (transcriptionResponse != null) {
                nuggetSharedViewModel.setTranscriptionEnded(
                    transcriptionResponse.resultsEntity?.channels?.get(0)?.alternatives?.get(
                        0
                    )?.transcript ?: ""
                )
                val orderEntity = OrderEntity(name = "Nothing", quantity = 1)
                val orderList = arrayListOf(orderEntity)
                val textToResponseRequestBody = TextToResponseRequestBody(
                    userPrompt = (transcriptionResponse.resultsEntity?.channels?.get(0)?.alternatives?.get(
                        0
                    )?.transcript ?: "").toString(), orderEntity = orderList, screenState = 3
                )
                viewModel.textToResponse(
                    body = textToResponseRequestBody
                )
//                val imagePath = getImageAsset()
//                val file = File(imagePath)
//                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                val imagePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
//                viewModel.refill(
//                    image = imagePart
//                )
            }
        }.launchIn(lifecycleScope)

        viewModel.textToResponse.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { txtToResponse ->
            if (txtToResponse != null) {
                nuggetSharedViewModel.setTextToResponseEnded(txtToResponse.intents)
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleState(state: AudioTranscriptionState) {
        when (state) {
            is AudioTranscriptionState.IsLoading -> handleLoading(state.isLoading)
            is AudioTranscriptionState.ShowToast -> Toast.makeText(
                this@MainActivity, state.message, Toast.LENGTH_SHORT
            ).show()

            is AudioTranscriptionState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {

        } else {

        }
    }

    private fun translateVoice(path: String) {
        val keywordsList = listOf(
            "drinks", "food", "caesar", "wedge", "caprese",
            "pork", "fish", "beef", "salmon", "steak",
            "chicken", "pina", "colada", "mojito", "margarita",
            "mile", "high", "coke", "maverick", "wingman",
            "martini", "iceman", "fudge", "brownie", "cheesecake"
        )
        val wavFile = File(path)
        val requestBody = wavFile.asRequestBody("audio/*".toMediaTypeOrNull())
        viewModel.transcribeAudio(
            model = deepGrammodel,
            smartFormat = smartFormat,
            language = deepGramlanguage,
            audio = requestBody, keywords = keywordsList
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotAmplitudeEvent(event: Events.RecordingAmplitude) {
        val amplitude = event.amplitude
        Log.e("AmplitudeHere---->", "gotAmplitudeEvent: $amplitude---$pauseCounter")
        if (initialAmplitudes.size < 40) {
            initialAmplitudes.add(amplitude)
        } else if (threshold == 0) {
            threshold = calculateThreshold(initialAmplitudes)
            Log.e("Threshold", "Dynamically calculated threshold: $threshold")
        }
        if (amplitude < threshold) {
            pauseCounter++
            if (pauseCounter == 15) {
                handlePause()
            }
        } else {
            pauseCounter = 0
        }
    }

    /**
     *@author MudassirSatti Calculated the Dynamic Threshold with initial Amplitude values
     */
    private fun calculateThreshold(amplitudes: List<Int>): Int {
        val sum = amplitudes.sum()
        val thresholdInner = sum / amplitudes.size
        val result = if (thresholdInner < 2600) {
            2800
        } else {
            sum / amplitudes.size
        }
        return result
    }

    private fun stopRecordingService() {
        stopService(Intent(this@MainActivity, NuggetRecorderService::class.java))
        nuggetSharedViewModel.setRecordingEnded("Ended")
    }

    override fun onDestroy() {
        super.onDestroy()
        bus?.unregister(this)
        Log.e(TAG_NUGET_MVP, "onDestroy: ")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun recordingSaved(event: Events.RecordingSaved) {
        Log.e(TAG_NUGET_MVP, "recordingCompleted: RecordingCompletedPathHere-->${event.uri}")
        translateVoice(event.uri)
        nuggetSharedViewModel.setTranscriptionStarted()
    }

    private fun handlePause() {
        stopRecordingService()
        pauseCounter = 0
        isFirstTime = true
        threshold = 0
        initialAmplitudes.clear()
    }

    //----Handle if already path exists no need to write temp file
    @Throws(IOException::class)
    fun getImageAsset(): String {
        val file = File.createTempFile("tempImage", ".png")
        val filePath = file.absolutePath
        val inputStream: InputStream = assets.open("test_glass.jpeg")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val out = FileOutputStream(filePath)
        out.write(buffer)
        out.close()
        return filePath
    }


}