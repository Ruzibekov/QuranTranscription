package com.ruzibekov.quran.transcription.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val FAVORITES_DATASTORE = "favorites_preferences"
private val Context.favoritesDataStore by preferencesDataStore(name = FAVORITES_DATASTORE)

@Singleton
class FavoritesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val favoriteIdsKey = stringSetPreferencesKey("favorite_surah_ids")

    val favoriteIdsFlow: Flow<Set<Int>> = context.favoritesDataStore.data
        .map { prefs ->
            prefs[favoriteIdsKey]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    suspend fun toggleFavorite(id: Int) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[favoriteIdsKey]?.toMutableSet() ?: mutableSetOf()
            val idString = id.toString()
            if (!current.add(idString)) {
                current.remove(idString)
            }
            prefs[favoriteIdsKey] = current
        }
    }
}
