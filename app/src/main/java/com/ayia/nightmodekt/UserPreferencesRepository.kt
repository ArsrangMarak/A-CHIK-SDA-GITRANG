package com.ayia.nightmodekt

import android.content.Context

class UserPreferencesRepository(private val context: Context) {

    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Get the appTheme. By default, the theme is set to follow the system.
     */
    val appTheme: Theme
        get() {
            val theme = sharedPreferences.getString(PREF_NAME_THEME_MODE, Theme.FOLLOW_SYSTEM.name)
            return Theme.valueOf(theme ?: Theme.FOLLOW_SYSTEM.name)
        }

    fun updateTheme(theme: Theme) {
        sharedPreferences.edit()
            .putString(PREF_NAME_THEME_MODE, theme.name)
            .apply()

        ThemeChanger().invoke(theme)
    }

    private val TEXT_SIZE_KEY = "text_size_key"

    // Save the text size to SharedPreferences
    fun saveTextSize(textSize: Float) {
        sharedPreferences.edit().putFloat(TEXT_SIZE_KEY, textSize).apply()
    }

    // Retrieve the saved text size from SharedPreferences
    fun getTextSize(): Float {
        return sharedPreferences.getFloat(TEXT_SIZE_KEY, 16f) // Return the default value if not found
    }

    companion object {
        private const val PREFS_NAME = "MyAppPreferences"
        private const val PREF_NAME_THEME_MODE = "theme_mode"

        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }
                val instance = UserPreferencesRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
