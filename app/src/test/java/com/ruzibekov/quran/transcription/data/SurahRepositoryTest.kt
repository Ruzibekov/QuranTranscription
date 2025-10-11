package com.ruzibekov.quran.transcription.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SurahRepositoryTest {

    private val repository = SurahRepository()

    @Test
    fun surahCount_isComplete() {
        assertEquals(114, repository.getSurahs().size)
    }

    @Test
    fun surahIds_areSequential() {
        val expectedIds = (1..114).toList()
        val actualIds = repository.getSurahs().map { it.id }
        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun getSurahById_returnsMatchingEntry() {
        val surah = repository.getSurahById(1)
        assertNotNull(surah)
        assertEquals("Al-Faatiha", surah.latinName)
    }
}

