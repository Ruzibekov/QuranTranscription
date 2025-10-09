package com.ruzibekov.quran.transcription.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ruzibekov.quran.transcription.data.Surah
import com.ruzibekov.quran.transcription.data.SurahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SurahDetailViewModel(
    private val surahId: Int,
    private val repository: SurahRepository = SurahRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurahDetailUiState())
    val uiState: StateFlow<SurahDetailUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = SurahDetailUiState(surah = repository.getSurahById(surahId))
    }

    companion object {
        const val ARG_SURAH_ID = "surahId"

        fun provideFactory(
            surahId: Int,
            repository: SurahRepository = SurahRepository(),
        ) = viewModelFactory {
            initializer {
                SurahDetailViewModel(
                    surahId = surahId,
                    repository = repository,
                )
            }
        }
    }
}

data class SurahDetailUiState(
    val surah: Surah? = null,
)
