package com.jalsanchay.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("jalsanchay_prefs", Context.MODE_PRIVATE)

    // Household Configuration
    var roofAreaM2: Double
        get() = prefs.getFloat("roof_area", 80f).toDouble()
        set(value) = prefs.edit().putFloat("roof_area", value.toFloat()).apply()

    var tankCapacityLiters: Double
        get() = prefs.getFloat("tank_capacity", 1250f).toDouble()
        set(value) = prefs.edit().putFloat("tank_capacity", value.toFloat()).apply()

    var dailyUsageLiters: Double
        get() = prefs.getFloat("daily_usage", 200f).toDouble()
        set(value) = prefs.edit().putFloat("daily_usage", value.toFloat()).apply()

    var defaultRunoffCoefficient: Double
        get() = prefs.getFloat("runoff_coeff", 0.85f).toDouble()
        set(value) = prefs.edit().putFloat("runoff_coeff", value.toFloat()).apply()
        
    var roofType: String
        get() = prefs.getString("roof_type", "Concrete") ?: "Concrete"
        set(value) {
            prefs.edit().putString("roof_type", value).apply()
            // Auto update coefficient
            defaultRunoffCoefficient = when(value) {
                "Metal" -> 0.90
                "Tile" -> 0.95
                else -> 0.85
            }
        }
        
    var isFarmerMode: Boolean
        get() = prefs.getBoolean("farmer_mode", false)
        set(value) = prefs.edit().putBoolean("farmer_mode", value).apply()

    // Onboarding & First Launch
    fun isFirstLaunch(): Boolean = prefs.getBoolean("first_launch", true)
    fun setFirstLaunchComplete() = prefs.edit().putBoolean("first_launch", false).apply()

    // Dark Mode Theme
    fun isDarkModeEnabled(): Boolean = prefs.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) = prefs.edit().putBoolean("dark_mode", enabled).apply()

    // Notifications
    fun isNotificationsEnabled(): Boolean = prefs.getBoolean("notifications_enabled", true)
    fun setNotificationsEnabled(enabled: Boolean) = prefs.edit().putBoolean("notifications_enabled", enabled).apply()

    // Data Export
    fun getLastExportTime(): Long = prefs.getLong("last_export", 0L)
    fun setLastExportTime(timestamp: Long) = prefs.edit().putLong("last_export", timestamp).apply()

    // Firebase & FCM
    fun setFCMToken(token: String) = prefs.edit().putString("fcm_token", token).apply()
    fun getFCMToken(): String = prefs.getString("fcm_token", "") ?: ""
    
    fun setUserIdForAnalytics(userId: String) = prefs.edit().putString("user_id", userId).apply()
    fun getUserId(): String = prefs.getString("user_id", "") ?: ""
    fun setLoggedInUser(userId: String) = setUserIdForAnalytics(userId)
    fun clearLoggedInUser() = prefs.edit().remove("user_id").apply()
    fun getLoggedInUserDisplayName(): String {
        val email = getUserId()
        if (email.isBlank()) return "Water Hero"
        val username = email.substringBefore("@")
        return username.split('.', '_', '-').joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
    }
    
    fun setAnalyticsEnabled(enabled: Boolean) = prefs.edit().putBoolean("analytics_enabled", enabled).apply()
    fun isAnalyticsEnabled(): Boolean = prefs.getBoolean("analytics_enabled", true)
    
    fun setCrashlyticsEnabled(enabled: Boolean) = prefs.edit().putBoolean("crashlytics_enabled", enabled).apply()
    fun isCrashlyticsEnabled(): Boolean = prefs.getBoolean("crashlytics_enabled", true)

    // Weather API
    fun setWeatherAPIKey(key: String) = prefs.edit().putString("weather_api_key", key).apply()
    fun getWeatherAPIKey(): String = prefs.getString("weather_api_key", "") ?: ""
    
    fun setLastLocationLatitude(lat: Double) = prefs.edit().putFloat("last_lat", lat.toFloat()).apply()
    fun getLastLocationLatitude(): Double = prefs.getFloat("last_lat", 28.7041f).toDouble()
    
    fun setLastLocationLongitude(lon: Double) = prefs.edit().putFloat("last_lon", lon.toFloat()).apply()
    fun getLastLocationLongitude(): Double = prefs.getFloat("last_lon", 77.1025f).toDouble()
    
    fun setAutoFillEnabled(enabled: Boolean) = prefs.edit().putBoolean("auto_fill_enabled", enabled).apply()
    fun isAutoFillEnabled(): Boolean = prefs.getBoolean("auto_fill_enabled", false)

    // Clear all data
    fun clearAll() = prefs.edit().clear().apply()
}
