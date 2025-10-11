package com.ruzibekov.quran.transcription.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ruzibekov.quran.transcription.data.Surah
import com.ruzibekov.quran.transcription.data.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: SurahRepository,
) : ViewModel() {

    private val surahId: Int = requireNotNull(savedStateHandle[ARG_SURAH_ID]) {
        "Argument $ARG_SURAH_ID is required"
    }

    private val _uiState = MutableStateFlow(SurahDetailUiState())
    val uiState: StateFlow<SurahDetailUiState> = _uiState.asStateFlow()

    init {
        val surah = repository.getSurahById(surahId)
        _uiState.value = SurahDetailUiState(
            surah = surah,
            nextSurahId = repository.getNextSurahId(surahId),
        )
    }

    companion object {
        const val ARG_SURAH_ID = "surahId"
    }
}

data class SurahDetailUiState(
    val surah: Surah? = null,
    val nextSurahId: Int? = null,
)
