package com.ayia.nightmodekt

data class SongLyric(
    val id: Int,
    val title: String,
    val titleInAnotherLanguage: String?,
    val lyrics: String,
    val lyricsInAnotherLanguage: String?,
    val audioResourceId: Int // Add this field
)
