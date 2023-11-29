package com.ayia.nightmodekt

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LyricsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SONG_TITLE = "extra_song_title"
        const val EXTRA_SONG_LYRICS = "extra_song_lyrics"
        const val EXTRA_SONG_ID = "extra_song_id"
    }

    private lateinit var userPrefs: UserPreferencesRepository

    private var songId: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyrics)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable the back button

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val lyricsTextView = findViewById<TextView>(R.id.lyricsTextView)

        userPrefs = UserPreferencesRepository.getInstance(this)

        songId = intent.getIntExtra(EXTRA_SONG_ID, 0)
        val songTitle = intent.getStringExtra(EXTRA_SONG_TITLE)
        val songLyrics = intent.getStringExtra(EXTRA_SONG_LYRICS)

        titleTextView.text = "$songId    $songTitle"
        lyricsTextView.text = songLyrics

        // Retrieve saved text size from user preferences
        textSize = userPrefs.getTextSize()

        // Apply the text size to the lyricsTextView
        updateTextSize()
        updateTextColor()
    }

    private var textSize = 16f // Default text size

    private fun increaseTextSize() {
        textSize += 2f
        updateTextSize()
    }

    private fun decreaseTextSize() {
        textSize -= 2f
        updateTextSize()
    }

    private fun updateTextSize() {
        val lyricsTextView = findViewById<TextView>(R.id.lyricsTextView)
        lyricsTextView.textSize = textSize
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Save the current text size to user preferences to retain it across recreation
        userPrefs.saveTextSize(textSize)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.header_menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Handle the back button click
                true
            }
            R.id.menu_increase_text_size -> {
                increaseTextSize()
                true
            }
            R.id.menu_decrease_text_size -> {
                decreaseTextSize()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showThemeDialog() {
        val themeOptions = arrayOf("Light Mode", "Dark Mode", "Follow System")

        val selectedThemeIndex = getSelectedThemeIndex()

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Theme")
            .setSingleChoiceItems(themeOptions, selectedThemeIndex) { dialog, position ->
                val selectedTheme = when (position) {
                    0 -> Theme.LIGHT_MODE
                    1 -> Theme.DARK_MODE
                    else -> Theme.FOLLOW_SYSTEM
                }
                saveTheme(selectedTheme)
                dialog.dismiss()
            }
            .show()
    }

    private fun getSelectedThemeIndex(): Int {
        val currentTheme = userPrefs.appTheme
        return when (currentTheme) {
            Theme.LIGHT_MODE -> 0
            Theme.DARK_MODE -> 1
            else -> 2
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun saveTheme(theme: Theme) {
        userPrefs.updateTheme(theme)
        ThemeChanger().invoke(theme)
        recreate()
        updateTextColor()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateTextColor() {

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val lyricsTextView = findViewById<TextView>(R.id.lyricsTextView)

        val textColorRes = when (userPrefs.appTheme) {
            Theme.LIGHT_MODE -> R.color.textColorPrimary // Set the color for the light mode theme
            Theme.DARK_MODE -> R.color.white // Set the color for the dark mode theme
            else -> {
                // Follow System theme
                if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                    android.R.color.primary_text_dark // Use default system text color for dark theme
                } else {
                    android.R.color.primary_text_light // Use default system text color for light theme
                }
            }
        }
        titleTextView.setTextColor(resources.getColor(textColorRes, null))
        lyricsTextView.setTextColor(resources.getColor(textColorRes, null))
    }
}
