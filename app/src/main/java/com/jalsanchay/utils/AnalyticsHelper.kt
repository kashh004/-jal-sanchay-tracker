package com.jalsanchay.utils

import com.jalsanchay.data.models.RainfallEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Advanced analytics calculations for dashboard
 */
class AnalyticsHelper {
    
    companion object {
        fun calculateAverageRainfall(entries: List<RainfallEntry>): Double {
            if (entries.isEmpty()) return 0.0
            return entries.map { it.rainfallMm }.average()
        }
        
        fun calculateMaxRainfall(entries: List<RainfallEntry>): Double {
            if (entries.isEmpty()) return 0.0
            return entries.maxOfOrNull { it.rainfallMm } ?: 0.0
        }
        
        fun calculateTotalRainfallDays(entries: List<RainfallEntry>): Int {
            return entries.size
        }
        
        fun getWeeklyTrend(entries: List<RainfallEntry>): List<Double> {
            val trend = mutableListOf<Double>()
            val calendar = Calendar.getInstance()
            
            for (i in 6 downTo 0) {
                val date = calendar.apply { add(Calendar.DAY_OF_MONTH, -i) }
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.time)
                
                val weekTotal = entries
                    .filter { it.date == dateStr }
                    .sumOf { it.litersHarvested }
                
                trend.add(weekTotal)
            }
            
            return trend
        }
        
        fun getMonthlyStats(entries: List<RainfallEntry>): Map<String, Double> {
            val stats = mutableMapOf<String, Double>()
            val months = setOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            
            months.forEach { month ->
                val total = entries
                    .filter { it.date.contains("-") && it.date.split("-")[1].toIntOrNull() != null }
                    .filter { 
                        val dateComponents = it.date.split("-")
                        val monthNum = dateComponents.getOrNull(1)?.toIntOrNull() ?: 0
                        monthNum == months.indexOf(month) + 1
                    }
                    .sumOf { it.litersHarvested }
                stats[month] = total
            }
            
            return stats
        }
        
        fun getConsistencyScore(entries: List<RainfallEntry>): Int {
            if (entries.isEmpty()) return 0
            
            val dateSet = entries.map { it.date }.toSet()
            val totalPossibleDays = 365
            val consistency = (dateSet.size * 100) / totalPossibleDays
            
            return consistency.coerceIn(0, 100)
        }
        
        fun getEnvironmentalImpact(totalLiters: Double): Map<String, Double> {
            return mapOf(
                "co2_saved" to totalLiters * 0.005,
                "trees_equivalent" to totalLiters * 0.0002,
                "days_of_supply" to totalLiters / 200.0,
                "showers_equivalent" to totalLiters / 40.0
            )
        }
    }
}
