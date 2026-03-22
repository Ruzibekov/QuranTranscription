package com.ruzibekov.quran.transcription.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruzibekov.quran.transcription.data.Surah
import com.ruzibekov.quran.transcription.data.SurahRepository
import com.ruzibekov.quran.transcription.data.local.AudioPositionDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: SurahRepository,
    private val audioPositionDataSource: AudioPositionDataSource,
) : ViewModel() {

    private val surahId: Int = requireNotNull(savedStateHandle[ARG_SURAH_ID]) {
        "Argument $ARG_SURAH_ID is required"
    }

    private val _uiState = MutableStateFlow(SurahDetailUiState())
    val uiState: StateFlow<SurahDetailUiState> = _uiState.asStateFlow()

    init {
        val surah = repository.getSurahById(surahId)
        val audioUrl = buildAudioUrl(surahId)
        val nextId = repository.getNextSurahId(surahId)
        _uiState.value = SurahDetailUiState(
            surah = surah,
            nextSurahId = nextId,
            nextSurahName = nextId?.let { repository.getSurahById(it)?.latinName },
            audioUrl = audioUrl,
        )
        viewModelScope.launch {
            val savedPosition = audioPositionDataSource.getPosition(surahId).first()
            _uiState.value = _uiState.value.copy(savedPositionMs = savedPosition)
        }
    }

    fun saveAudioPosition(positionMs: Long) {
        viewModelScope.launch {
            audioPositionDataSource.savePosition(surahId, positionMs)
        }
    }

    fun increaseFontSize() {
        _uiState.value = _uiState.value.copy(
            fontSizeSp = (_uiState.value.fontSizeSp + 1).coerceAtMost(22),
        )
    }

    fun decreaseFontSize() {
        _uiState.value = _uiState.value.copy(
            fontSizeSp = (_uiState.value.fontSizeSp - 1).coerceAtLeast(14),
        )
    }

    fun toggleFullScreen() {
        viewModelScope.launch {
            val currentPos = audioPositionDataSource.getPosition(surahId).first()
            _uiState.value = _uiState.value.copy(
                isFullScreen = !_uiState.value.isFullScreen,
                savedPositionMs = currentPos,
            )
        }
    }

    fun updateCurrentAyah(index: Int) {
        _uiState.value = _uiState.value.copy(currentAyahIndex = index)
    }

    fun seekToAyah(index: Int) {
        _uiState.value = _uiState.value.copy(seekToAyahIndex = index)
    }

    fun clearSeek() {
        _uiState.value = _uiState.value.copy(seekToAyahIndex = -1)
    }

    private fun buildAudioUrl(id: Int): String {
        val paddedId = id.toString().padStart(3, '0')
        return "https://server8.mp3quran.net/afs/$paddedId.mp3"
    }

    companion object {
        const val ARG_SURAH_ID = "surahId"
    }
}

data class SurahDetailUiState(
    val surah: Surah? = null,
    val nextSurahId: Int? = null,
    val nextSurahName: String? = null,
    val audioUrl: String = "",
    val savedPositionMs: Long = 0L,
    val fontSizeSp: Int = 17,
    val isFullScreen: Boolean = false,
    val currentAyahIndex: Int = -1,
    val seekToAyahIndex: Int = -1,
)
