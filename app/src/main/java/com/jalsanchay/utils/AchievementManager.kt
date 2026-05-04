package com.jalsanchay.utils

import android.content.Context

/**
 * Manages user achievements and milestones
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val threshold: Double,
    val metric: String // "liters", "days", "entries"
)

class AchievementManager(private val context: Context) {
    private val prefs = PreferencesHelper(context)

    companion object {
        val ACHIEVEMENTS = listOf(
            Achievement(
                "first_entry",
                "First Step",
                "Log your first rainfall entry",
                "🌧️",
                1.0,
                "entries"
            ),
            Achievement(
                "hundred_liters",
                "Water Warrior",
                "Harvest 100 liters of water",
                "💪",
                100.0,
                "liters"
            ),
            Achievement(
                "thousand_liters",
                "Conservation Champion",
                "Harvest 1000 liters of water",
                "🏆",
                1000.0,
                "liters"
            ),
            Achievement(
                "ten_entries",
                "Dedicated Logger",
                "Log 10 rainfall entries",
                "📝",
                10.0,
                "entries"
            ),
            Achievement(
                "fifty_entries",
                "Data Master",
                "Log 50 rainfall entries",
                "📊",
                50.0,
                "entries"
            ),
            Achievement(
                "tree_saver",
                "Tree Saver",
                "Save equivalent water of 1 tree",
                "🌱",
                0.2,
                "liters"
            ),
            Achievement(
                "carbon_neutral",
                "Carbon Neutral",
                "Offset 10 kg of CO₂",
                "♻️",
                10.0,
                "liters"
            ),
            Achievement(
                "week_streak",
                "Weekly Warrior",
                "Log rainfall for 7 consecutive days",
                "🔥",
                7.0,
                "days"
            ),
            Achievement(
                "eco_champion",
                "Eco Champion",
                "Harvest 5000 liters of water",
                "🌍",
                5000.0,
                "liters"
            ),
            Achievement(
                "perfect_score",
                "Perfect Score",
                "Achieve 100% impact score",
                "⭐",
                100.0,
                "score"
            )
        )
    }

    fun getUnlockedAchievements(totalLiters: Double, entryCount: Int): List<Achievement> {
        return ACHIEVEMENTS.filter { achievement ->
            when (achievement.metric) {
                "liters" -> totalLiters >= achievement.threshold
                "entries" -> entryCount >= achievement.threshold.toInt()
                "score" -> totalLiters / 20 >= achievement.threshold
                else -> false
            }
        }
    }

    fun getNextAchievements(totalLiters: Double, entryCount: Int): List<Pair<Achievement, Double>> {
        return ACHIEVEMENTS
            .filterNot { achievement ->
                when (achievement.metric) {
                    "liters" -> totalLiters >= achievement.threshold
                    "entries" -> entryCount >= achievement.threshold.toInt()
                    else -> false
                }
            }
            .map { achievement ->
                val progress = when (achievement.metric) {
                    "liters" -> totalLiters / achievement.threshold
                    "entries" -> entryCount / achievement.threshold
                    else -> 0.0
                }
                Pair(achievement, progress.coerceIn(0.0, 1.0))
            }
    }

    fun getAchievementCount(): Int = ACHIEVEMENTS.size
}
