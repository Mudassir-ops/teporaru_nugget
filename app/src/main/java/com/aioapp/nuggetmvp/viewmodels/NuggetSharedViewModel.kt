package com.aioapp.nuggetmvp.viewmodels

import androidx.lifecycle.ViewModel
import com.aioapp.nuggetmvp.R
import com.aioapp.nuggetmvp.models.Food
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
    val allMenuItemList: List<Food> = listOf(
        Food(R.drawable.caesar, R.drawable.ceasar_full_img, "Caesar", "12"),
        Food(R.drawable.wedge, R.drawable.wedge_full_img, "Wedge", "14"),
        Food(R.drawable.caprese, R.drawable.caprese_full_img, "Caprese", "14"),
        Food(R.drawable.pork, R.drawable.pork_full_img, "Pork", "18"),
        Food(R.drawable.fish, R.drawable.fish_full_img, "Fish", "18"),
        Food(R.drawable.beef, R.drawable.beef_full_img, "Beef", "18"),
        Food(R.drawable.salmon, R.drawable.salmon_full_img, "Salmon", "28"),
        Food(R.drawable.steak, R.drawable.steak_full_img, "Steak", "35"),
        Food(R.drawable.chicken, R.drawable.chicken_full_img, "Chicken", "25"),
        Food(R.drawable.pina_colada, R.drawable.pina_colada_full_img, "Pina Colada", "24"),
        Food(R.drawable.mojito, R.drawable.mojito_full_img, "Mojito", "24"),
        Food(R.drawable.margarita, R.drawable.margaritta_full_img, "Margarita", "24"),
        Food(R.drawable.mile_high, R.drawable.mile_high_full_img, "Mile High", "14"),
        Food(R.drawable.coke, R.drawable.coke_full_img, "Coke", "14"),
        Food(R.drawable.maverick, R.drawable.mavrick_full_img, "Maverick", "14"),
        Food(R.drawable.wingman, R.drawable.wingman_full_img, "Wingman", "14"),
        Food(R.drawable.martini, R.drawable.martini_full_img, "Martini", "14"),
        Food(R.drawable.iceman, R.drawable.iceman_full_img, "Iceman", "14")
    )

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
