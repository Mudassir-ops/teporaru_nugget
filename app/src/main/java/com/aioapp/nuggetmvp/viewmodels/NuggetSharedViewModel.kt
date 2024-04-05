package com.aioapp.nuggetmvp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aioapp.nuggetmvp.di.usecase.GetAllMenuUseCase
import com.aioapp.nuggetmvp.models.Food
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import com.aioapp.nuggetmvp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NuggetSharedViewModel @Inject constructor(
    private var getAllMenuUseCase: GetAllMenuUseCase
) : ViewModel() {
    private val state = MutableStateFlow<NuggetProcessingStatus>(NuggetProcessingStatus.Init)
    val mState: StateFlow<NuggetProcessingStatus> get() = state

    private val _allMenuItemsResponse = MutableStateFlow<List<Food>>(arrayListOf())
    val allMenuItemsResponse: StateFlow<List<Food>> get() = _allMenuItemsResponse

    fun setRecordingStarted() {
        state.value = NuggetProcessingStatus.RecordingStarted(true)
    }

    fun setRecordingEnded(value: String) {
        state.value = NuggetProcessingStatus.RecordingEnded(value)
    }

    fun setTranscriptionStarted() {
        state.value = NuggetProcessingStatus.TranscriptStarted(true)
    }

    fun setTranscriptionEnded(transcriptionValue: String) {
        state.value = NuggetProcessingStatus.ParitialTranscriptionState(transcriptionValue)
    }

    fun setTextToResponseEnded(intentEntity: TextToResponseIntent?) {
        state.value = NuggetProcessingStatus.TextToResponseEnded(intentEntity)
    }

    init {
        getAllMenuData()
    }

    private fun getAllMenuData() {
        viewModelScope.launch {
            getAllMenuUseCase.invokeAllMenuDataUseCase().onStart {}.catch { exception ->
                exception.printStackTrace()
            }.collect { result ->
                when (result) {
                    is Result.Success -> {
                        val newData = result.data
                        if (newData != _allMenuItemsResponse.value) {
                            _allMenuItemsResponse.value = newData
                        }
                    }

                    else -> {
//                         showToast("result.Some thing went Wrong")
                    }
                }

            }
        }
    }
}

sealed class NuggetProcessingStatus {
    data object Init : NuggetProcessingStatus()
    data class RecordingStarted(val isStarted: Boolean) : NuggetProcessingStatus()
    data class RecordingEnded(val isEnded: String) : NuggetProcessingStatus()
    data class TranscriptStarted(val isStarted: Boolean) : NuggetProcessingStatus()
    data class ParitialTranscriptionState(val value: String) : NuggetProcessingStatus()
    data class TextToResponseEnded(val value: TextToResponseIntent?) :
        NuggetProcessingStatus()
}
