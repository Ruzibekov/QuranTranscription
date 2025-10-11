package com.ruzibekov.quran.transcription.ui.screens.home

import androidx.lifecycle.ViewModel
import com.ruzibekov.quran.transcription.data.Surah
import com.ruzibekov.quran.transcription.data.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: SurahRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = HomeUiState(surahs = repository.getSurahs())
    }
}

data class HomeUiState(
    val surahs: List<Surah> = emptyList(),
)
