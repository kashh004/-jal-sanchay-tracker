package com.jalsanchay.utils

import android.content.Context
import android.os.Bundle

/**
 * Dummy Firebase Configuration & Setup Manager
 * (Firebase dependencies are currently commented out in build.gradle)
 */
object FirebaseManager {
    
    fun initialize(context: Context) {
        AppLogger.i("Firebase is currently disabled")
    }
    
    fun logEvent(eventName: String, params: Bundle? = null) {
        AppLogger.d("Analytics event (disabled): $eventName")
    }
    
    fun logRainfallEntry(rainfallMm: Float, litersHarvested: Double) {
        logEvent("rainfall_entry")
    }
    
    fun logAchievementUnlocked(achievementName: String) {
        logEvent("achievement_unlocked")
    }
    
    fun logDataExport(format: String, entriesCount: Int) {
        logEvent("data_exported")
    }
    
    fun logFeatureUsage(featureName: String) {
        logEvent("feature_used")
    }
    
    fun recordException(exception: Exception) {
        AppLogger.e("Exception (Crashlytics disabled): ${exception.message}")
    }
    
    fun setCrashCustomKey(key: String, value: String) {}
    
    fun getFCMToken(onTokenReceived: (String) -> Unit) {
        onTokenReceived("")
    }
    
    fun subscribeTopic(topic: String) {}
    
    fun unsubscribeTopic(topic: String) {}
    
    fun setAnalyticsCollection(enabled: Boolean) {}
}
