package com.aioapp.nuggetmvp.viewmodels

import androidx.lifecycle.ViewModel
import com.aioapp.nuggetmvp.models.TextToResponseIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NuggetSharedViewModel @Inject constructor(
) : ViewModel() {
    private val state = MutableStateFlow<NuggetProcessingStatus>(NuggetProcessingStatus.Init)
    val mState: StateFlow<NuggetProcessingStatus> get() = state

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
        state.value = NuggetProcessingStatus.TranscriptEnd(transcriptionValue)
    }

    fun setTextToResponseEnded(intentEntity: ArrayList<TextToResponseIntent>?) {
        state.value = NuggetProcessingStatus.TextToResponseEnded(intentEntity)
    }
}

sealed class NuggetProcessingStatus {
    data object Init : NuggetProcessingStatus()
    data class RecordingStarted(val isStarted: Boolean) : NuggetProcessingStatus()
    data class RecordingEnded(val isEnded: String) : NuggetProcessingStatus()
    data class TranscriptStarted(val isStarted: Boolean) : NuggetProcessingStatus()
    data class TranscriptEnd(val value: String) : NuggetProcessingStatus()
    data class TextToResponseEnded(val value: ArrayList<TextToResponseIntent>?) :
        NuggetProcessingStatus()
}
