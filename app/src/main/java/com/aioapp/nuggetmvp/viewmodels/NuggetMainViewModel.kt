package com.aioapp.nuggetmvp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aioapp.nuggetmvp.di.usecase.RefillUseCase
import com.aioapp.nuggetmvp.di.usecase.TextToResponseUseCase
import com.aioapp.nuggetmvp.di.usecase.TranscribeAudioWithDeepGramUseCase
import com.aioapp.nuggetmvp.models.RefillResponseEntity
import com.aioapp.nuggetmvp.models.TextToResponseEntity
import com.aioapp.nuggetmvp.models.TextToResponseRequestBody
import com.aioapp.nuggetmvp.models.TranscriptionBaseResponse
import com.aioapp.nuggetmvp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class NuggetMainViewModel @Inject constructor(
    private val deepGramUseCase: TranscribeAudioWithDeepGramUseCase,
    private val textToResponseUseCase: TextToResponseUseCase,
    private val refillUseCase: RefillUseCase
) : ViewModel() {

    private val _deepGramResponse = MutableStateFlow<TranscriptionBaseResponse?>(null)
    val deepGramResponse: StateFlow<TranscriptionBaseResponse?> get() = _deepGramResponse

    private val _textToResponse = MutableStateFlow<TextToResponseEntity?>(null)
    val textToResponse: StateFlow<TextToResponseEntity?> get() = _textToResponse


    private val _textToResponseStream = MutableStateFlow<String?>(null)
    val textToResponseStream: StateFlow<String?> get() = _textToResponseStream


    private val _refillResponse = MutableStateFlow<RefillResponseEntity?>(null)
    val refillResponse: StateFlow<RefillResponseEntity?> get() = _refillResponse

    private val state = MutableStateFlow<AudioTranscriptionState>(AudioTranscriptionState.Init)
    val mState: StateFlow<AudioTranscriptionState> get() = state

    private val _itemResponseStates = MutableLiveData<String?>(null)
    val itemResponseStates: LiveData<String?> get() = _itemResponseStates



    private fun setLoading() {
        state.value = AudioTranscriptionState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = AudioTranscriptionState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = AudioTranscriptionState.ShowToast(message)
    }

    fun transcribeAudio(
        model: String?,
        smartFormat: Boolean?,
        language: String?,
        audio: RequestBody,
        keywords: List<String>
    ) {
        viewModelScope.launch {
            deepGramUseCase.invokeTranscribeAudioUseCase(
                model = model,
                smartFormat = smartFormat,
                language = language,
                audio = audio,
                keywords = keywords
            ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.message.toString())
                exception.printStackTrace()
            }.collect { result ->
                hideLoading()
                when (result) {
                    is Result.Success -> {
                        _deepGramResponse.value = result.data
                    }

                    else -> {
                        //   showToast("result.Some thing went Wrong")
                    }
                }
            }
        }
    }


    fun textToResponse(
        body: TextToResponseRequestBody
    ) {
        viewModelScope.launch {
            textToResponseUseCase.invokeStreamingResponse(
                body = body
            ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.message.toString())
                exception.printStackTrace()
            }.collect { result ->
                hideLoading()
                _textToResponseStream.value = result
                _itemResponseStates.value = result
            }
        }

//        viewModelScope.launch {
//            textToResponseUseCase.invokeTextToResponseUseCase(
//                body = body
//            ).onStart {
//                setLoading()
//            }.catch { exception ->
//                hideLoading()
//                showToast(exception.message.toString())
//                exception.printStackTrace()
//            }.collect { result ->
//                hideLoading()
//                when (result) {
//                    is Result.Success -> {
//                        _textToResponse.value = result.data
//                    }
//
//                    else -> {
//                        showToast("result.Some thing went Wrong")
//                    }
//                }
//            }
//        }
    }

    fun refill(
        image: MultipartBody.Part
    ) {
        viewModelScope.launch {
            refillUseCase.invokeTRefillUseCase(
                image = image
            ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.message.toString())
                exception.printStackTrace()
            }.collect { result ->
                hideLoading()
                when (result) {
                    is Result.Success -> {
                        _refillResponse.value = result.data
                    }

                    else -> {
                        showToast("result.Some thing went Wrong")
                    }
                }
            }
        }
    }
}

sealed class AudioTranscriptionState {
    data object Init : AudioTranscriptionState()
    data class IsLoading(val isLoading: Boolean) : AudioTranscriptionState()
    data class ShowToast(val message: String) : AudioTranscriptionState()
}