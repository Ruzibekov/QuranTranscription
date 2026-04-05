package com.ruzibekov.quran.transcription.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruzibekov.quran.transcription.data.Surah
import com.ruzibekov.quran.transcription.data.SurahRepository
import com.ruzibekov.quran.transcription.data.local.AudioPositionDataSource
import com.ruzibekov.quran.transcription.data.local.FavoritesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: SurahRepository,
    private val favoritesDataSource: FavoritesDataSource,
    audioPositionDataSource: AudioPositionDataSource,
) : ViewModel() {

    private val allSurahs: List<Surah> = repository.getSurahs()

    private val _uiState = MutableStateFlow(
        HomeUiState(
            surahs = allSurahs,
            favoriteIds = emptySet(),
        ),
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _scrollToTop = Channel<Unit>(Channel.CONFLATED)
    val scrollToTop = _scrollToTop.receiveAsFlow()

    init {
        viewModelScope.launch {
            favoritesDataSource.favoriteIdsFlow.collect { favorites ->
                _uiState.update { current ->
                    current.copy(
                        favoriteIds = favorites,
                        surahs = filterAndSort(
                            query = current.searchQuery,
                            favorites = favorites,
                            filter = current.selectedFilter,
                        ),
                    )
                }
            }
        }
        viewModelScope.launch {
            audioPositionDataSource.listenedSurahIdsFlow.collect { listened ->
                _uiState.update { it.copy(listenedIds = listened) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                surahs = filterAndSort(
                    query = query,
                    favorites = it.favoriteIds,
                    filter = it.selectedFilter,
                ),
            )
        }
    }

    fun selectFilter(filter: SurahFilter) {
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                surahs = filterAndSort(
                    query = it.searchQuery,
                    favorites = it.favoriteIds,
                    filter = filter,
                ),
            )
        }
        _scrollToTop.trySend(Unit)
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            favoritesDataSource.toggleFavorite(id)
        }
    }

    private fun filterAndSort(
        query: String,
        favorites: Set<Int>,
        filter: SurahFilter = SurahFilter.ALL,
    ): List<Surah> {
        return allSurahs
            .let { surahs ->
                if (query.isBlank()) surahs
                else surahs.filter {
                    it.latinName.contains(query, ignoreCase = true) ||
                        it.arabicName.contains(query, ignoreCase = true)
                }
            }
            .let { surahs ->
                when (filter) {
                    SurahFilter.ALL -> surahs
                    SurahFilter.FAVORITES -> surahs.filter { favorites.contains(it.id) }
                }
            }
            .sortedWith(
                compareByDescending<Surah> { favorites.contains(it.id) }
                    .thenBy { it.id },
            )
    }
}

enum class SurahFilter {
    ALL, FAVORITES
}

data class HomeUiState(
    val surahs: List<Surah> = emptyList(),
    val searchQuery: String = "",
    val favoriteIds: Set<Int> = emptySet(),
    val listenedIds: Set<Int> = emptySet(),
    val selectedFilter: SurahFilter = SurahFilter.ALL,
)
