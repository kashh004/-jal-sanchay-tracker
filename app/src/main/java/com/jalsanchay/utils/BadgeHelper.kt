package com.jalsanchay.utils

object BadgeHelper {
    fun getBadgeForLiters(liters: Double): String {
        return when {
            liters >= 5000 -> "🏆 Champion"
            liters >= 1000 -> "🌟 Saver"
            liters >= 100 -> "🌱 Beginner"
            else -> "💧 Starter"
        }
    }
}
