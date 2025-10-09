package com.ruzibekov.quran.transcription.data

class SurahRepository {

    private val surahs = listOf(
        Surah(
            id = 112,
            arabicName = "Al-Ikhlas",
            latinName = "Al-Ikhlos",
            transliteration = """
                Bismillahir rohmanir rohim
                Qul huvallohu ahad
                Allohus-samad
                Lam yalid va lam yulad
                Va lam yakun-lahu kufuwan ahad
            """.trimIndent(),
        ),
        Surah(
            id = 113,
            arabicName = "Al-Falaq",
            latinName = "Al-Falaq",
            transliteration = """
                Bismillahir rohmanir rohim
                Qul a'uzu bi rabbil-falaq
                Min sharri ma kholaq
                Va min sharri ghosiqlin iza vaqab
                Va min sharrin-naffathati fil-'uqad
                Va min sharri hasidin iza hasad
            """.trimIndent(),
        ),
        Surah(
            id = 114,
            arabicName = "An-Nas",
            latinName = "An-Nos",
            transliteration = """
                Bismillahir rohmanir rohim
                Qul a'uzu bi rabbin-nas
                Malikin-nas
                Ilahin-nas
                Min sharril waswasil khannas
                Alladhi yuwaswisu fi sudurin-nas
                Minal jinnati van-nas
            """.trimIndent(),
        ),
        Surah(
            id = 111,
            arabicName = "Al-Masad",
            latinName = "Al-Masad",
            transliteration = """
                Bismillahir rohmanir rohim
                Tabbat yada Abi Lahabin va tabb
                Ma aghna anhu maluhu va ma kasab
                Sayasla naran dhaata lahab
                Va imra'atuhu hammalatal-hatab
                Fi jidiha hablun mim-masad
            """.trimIndent(),
        ),
        Surah(
            id = 109,
            arabicName = "Al-Kafirun",
            latinName = "Al-Kofirun",
            transliteration = """
                Bismillahir rohmanir rohim
                Qul ya ayyuhal kofirun
                La a'budu ma ta'budun
                Va la antum abiduna ma a'bud
                Va la ana abidum ma 'abadtum
                Va la antum abiduna ma a'bud
                Lakum dinukum va liya din
            """.trimIndent(),
        ),
    )

    fun getSurahs(): List<Surah> = surahs

    fun getSurahById(id: Int): Surah? = surahs.firstOrNull { it.id == id }
}
