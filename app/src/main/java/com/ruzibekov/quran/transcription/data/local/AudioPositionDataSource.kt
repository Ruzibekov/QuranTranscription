package com.ruzibekov.quran.transcription.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val AUDIO_POSITION_DATASTORE = "audio_position_preferences"
private val Context.audioPositionDataStore by preferencesDataStore(name = AUDIO_POSITION_DATASTORE)

@Singleton
class AudioPositionDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun getPosition(surahId: Int): Flow<Long> =
        context.audioPositionDataStore.data.map { prefs ->
            prefs[positionKey(surahId)] ?: 0L
        }

    val listenedSurahIdsFlow: Flow<Set<Int>> =
        context.audioPositionDataStore.data.map { prefs ->
            prefs.asMap().keys
                .mapNotNull { key ->
                    val name = key.name
                    if (name.startsWith("position_")) {
                        val id = name.removePrefix("position_").toIntOrNull()
                        val pos = prefs[longPreferencesKey(name)] ?: 0L
                        if (id != null && pos > 0L) id else null
                    } else null
                }
                .toSet()
        }

    suspend fun savePosition(surahId: Int, positionMs: Long) {
        context.audioPositionDataStore.edit { prefs ->
            prefs[positionKey(surahId)] = positionMs
        }
    }

    private fun positionKey(surahId: Int) = longPreferencesKey("position_$surahId")
}
