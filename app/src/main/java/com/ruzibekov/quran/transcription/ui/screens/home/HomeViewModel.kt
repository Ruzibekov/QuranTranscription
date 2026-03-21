package com.ruzibekov.quran.transcription.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruzibekov.quran.transcription.data.Surah
import com.ruzibekov.quran.transcription.data.SurahRepository
import com.ruzibekov.quran.transcription.data.local.FavoritesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: SurahRepository,
    private val favoritesDataSource: FavoritesDataSource,
) : ViewModel() {

    private val allSurahs: List<Surah> = repository.getSurahs()

    private val _uiState = MutableStateFlow(
        HomeUiState(
            surahs = allSurahs,
            favoriteIds = emptySet(),
        ),
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            favoritesDataSource.favoriteIdsFlow.collect { favorites ->
                _uiState.update { current ->
                    current.copy(
                        favoriteIds = favorites,
                        surahs = filterAndSort(query = current.searchQuery, favorites = favorites, favoritesOnly = current.showFavoritesOnly),
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                surahs = filterAndSort(query = query, favorites = it.favoriteIds, favoritesOnly = it.showFavoritesOnly),
            )
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            favoritesDataSource.toggleFavorite(id)
        }
    }

    fun toggleFavoritesFilter() {
        _uiState.update {
            val newFilter = !it.showFavoritesOnly
            it.copy(
                showFavoritesOnly = newFilter,
                surahs = filterAndSort(query = it.searchQuery, favorites = it.favoriteIds, favoritesOnly = newFilter),
            )
        }
    }

    private fun filterAndSort(query: String, favorites: Set<Int>, favoritesOnly: Boolean = false): List<Surah> {
        return allSurahs
            .let { surahs ->
                if (query.isBlank()) surahs
                else surahs.filter { it.latinName.contains(query, ignoreCase = true) || it.arabicName.contains(query, ignoreCase = true) }
            }
            .let { surahs ->
                if (favoritesOnly) surahs.filter { favorites.contains(it.id) }
                else surahs
            }
            .sortedWith(
                compareByDescending<Surah> { favorites.contains(it.id) }
                    .thenBy { it.id },
            )
    }
}

data class HomeUiState(
    val surahs: List<Surah> = emptyList(),
    val searchQuery: String = "",
    val favoriteIds: Set<Int> = emptySet(),
    val showFavoritesOnly: Boolean = false,
)
