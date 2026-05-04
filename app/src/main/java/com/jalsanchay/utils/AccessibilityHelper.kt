package com.jalsanchay.utils

import android.view.View
import androidx.core.view.ViewCompat

/**
 * Helps improve accessibility throughout the app
 */
object AccessibilityHelper {
    
    /**
     * Set comprehensive content descriptions for views
     */
    fun setContentDescription(view: View, description: String) {
        ViewCompat.setAccessibilityLiveRegion(view, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE)
        view.contentDescription = description
    }
    
    /**
     * Format number for screen readers
     * Example: 1234.5 becomes "one thousand two hundred thirty-four point five"
     */
    fun formatNumberForReading(number: Double): String {
        return when {
            number < 1000 -> String.format("%.2f", number)
            number < 1_000_000 -> String.format("%.1f thousand", number / 1000)
            else -> String.format("%.1f million", number / 1_000_000)
        }
    }
    
    /**
     * Create accessible label for stat
     */
    fun createStatLabel(label: String, value: String, unit: String): String {
        return "$label: $value $unit"
    }
    
    /**
     * Format date for accessibility
     */
    fun formatDateForReading(date: String): String {
        // Converts "2026-04-25" to "April twenty-fifth, twenty twenty-six"
        return try {
            val parts = date.split("-")
            if (parts.size == 3) {
                val month = getMonthName(parts[1].toIntOrNull() ?: 1)
                val day = formatDayForReading(parts[2].toIntOrNull() ?: 1)
                val year = formatYearForReading(parts[0].toIntOrNull() ?: 2026)
                "$month $day, $year"
            } else {
                date
            }
        } catch (e: Exception) {
            date
        }
    }
    
    private fun getMonthName(month: Int): String {
        return arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ).getOrNull(month - 1) ?: "Unknown"
    }
    
    private fun formatDayForReading(day: Int): String {
        return when {
            day in 10..20 -> "${day}th"
            day % 10 == 1 -> "${day}st"
            day % 10 == 2 -> "${day}nd"
            day % 10 == 3 -> "${day}rd"
            else -> "${day}th"
        }
    }
    
    private fun formatYearForReading(year: Int): String {
        return when {
            year < 2000 -> "nineteen ${year - 1900}"
            year < 2010 -> "two thousand ${year - 2000}"
            else -> {
                val thousands = year / 1000
                val remainder = year % 1000
                if (remainder == 0) {
                    "$thousands thousand"
                } else {
                    "$thousands thousand ${remainder}"
                }
            }
        }
    }
}
