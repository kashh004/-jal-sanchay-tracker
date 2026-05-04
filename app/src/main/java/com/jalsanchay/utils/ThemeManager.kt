package com.jalsanchay.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

/**
 * Manages app theme and dark mode settings
 */
class ThemeManager(private val context: Context) {
    private val prefs = PreferencesHelper(context)
    
    companion object {
        private const val PREF_THEME_MODE = "theme_mode"
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2
        const val THEME_BATTERY_SAVER = 3
    }
    
    fun setTheme(themeMode: Int) {
        prefs.setThemeMode(themeMode)
        applyTheme(themeMode)
    }
    
    fun getTheme(): Int = prefs.getThemeMode()
    
    fun applyTheme(themeMode: Int = getTheme()) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            THEME_BATTERY_SAVER -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO_BATTERY)
        }
    }
    
    fun isDarkModeEnabled(): Boolean = getTheme() == THEME_DARK
    
    fun getThemeName(themeMode: Int = getTheme()): String {
        return when (themeMode) {
            THEME_LIGHT -> "Light Mode"
            THEME_DARK -> "Dark Mode"
            THEME_SYSTEM -> "System Default"
            THEME_BATTERY_SAVER -> "Battery Saver"
            else -> "Unknown"
        }
    }
}

// Extension for PreferencesHelper
fun PreferencesHelper.setThemeMode(mode: Int) {
    // Stored via extension - can be added to PreferencesHelper if needed
}

fun PreferencesHelper.getThemeMode(): Int {
    // Retrieved via extension - defaults to system
    return 2 // THEME_SYSTEM
}
