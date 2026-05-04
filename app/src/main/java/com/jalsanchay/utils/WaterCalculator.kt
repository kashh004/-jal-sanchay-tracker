package com.jalsanchay.utils

object WaterCalculator {
    // Core formula: Area (m²) × Rainfall (mm) × 0.0929 × Runoff Coefficient = Litres
    fun calculateLiters(rainfallMm: Double, roofAreaM2: Double, runoffCoefficient: Double): Double {
        return roofAreaM2 * rainfallMm * 0.0929 * runoffCoefficient
    }

    fun householdWaterDays(liters: Double, dailyUsageLiters: Double): Double {
        if (dailyUsageLiters <= 0) return 0.0
        return liters / dailyUsageLiters
    }

    fun tankFillPercent(liters: Double, tankCapacityLiters: Double): Int {
        if (tankCapacityLiters <= 0) return 0
        return ((liters / tankCapacityLiters) * 100).toInt().coerceIn(0, 100)
    }

    fun impactScore(totalLiters: Double): Int =
        (totalLiters / 20).toInt().coerceIn(0, 100)

    fun co2Saved(totalLiters: Double): Double = totalLiters * 0.005

    fun treesEquivalent(totalLiters: Double): Double = totalLiters * 0.0002
}
