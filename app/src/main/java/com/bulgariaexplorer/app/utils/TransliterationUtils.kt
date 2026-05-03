package com.bulgariaexplorer.app.utils

object TransliterationUtils {

    private val cyrillicToLatin = mapOf(
        'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d",
        'е' to "e", 'ж' to "zh", 'з' to "z", 'и' to "i", 'й' to "y",
        'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n", 'о' to "o",
        'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t", 'у' to "u",
        'ф' to "f", 'х' to "h", 'ц' to "ts", 'ч' to "ch", 'ш' to "sh",
        'щ' to "sht", 'ъ' to "a", 'ь' to "y", 'ю' to "yu", 'я' to "ya",
        'А' to "A", 'Б' to "B", 'В' to "V", 'Г' to "G", 'Д' to "D",
        'Е' to "E", 'Ж' to "Zh", 'З' to "Z", 'И' to "I", 'Й' to "Y",
        'К' to "K", 'Л' to "L", 'М' to "M", 'Н' to "N", 'О' to "O",
        'П' to "P", 'Р' to "R", 'С' to "S", 'Т' to "T", 'У' to "U",
        'Ф' to "F", 'Х' to "H", 'Ц' to "Ts", 'Ч' to "Ch", 'Ш' to "Sh",
        'Щ' to "Sht", 'Ъ' to "A", 'Ь' to "Y", 'Ю' to "Yu", 'Я' to "Ya"
    )

    fun transliterate(text: String): String {
        val result = StringBuilder()
        for (char in text) {
            result.append(cyrillicToLatin[char] ?: char)
        }
        return result.toString()
    }

    fun matchesSearch(text: String?, query: String): Boolean {
        if (text.isNullOrEmpty()) return false

        val lowerQuery = query.lowercase()
        val lowerText = text.lowercase()

        // Direct match
        if (lowerText.contains(lowerQuery)) return true

        // Transliterated match (Cyrillic to Latin)
        val transliteratedText = transliterate(lowerText)
        if (transliteratedText.contains(lowerQuery)) return true

        return false
    }
}
