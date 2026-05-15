package com.jalsanchay.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class WaterCalculatorTest {

    @Test
    fun testCalculateLiters() {
        // Area (m²) = 100, Rainfall (mm) = 10, Runoff = 0.85
        // Expected: 100 * 10 * 0.0929 * 0.85 = 78.965
        val result = WaterCalculator.calculateLiters(10.0, 100.0, 0.85)
        assertEquals(78.965, result, 0.001)
    }

    @Test
    fun testHouseholdWaterDays() {
        // 1000 liters, 50 liters/day usage = 20 days
        val days = WaterCalculator.householdWaterDays(1000.0, 50.0)
        assertEquals(20.0, days, 0.0)

        // Zero usage should return 0
        val daysZero = WaterCalculator.householdWaterDays(1000.0, 0.0)
        assertEquals(0.0, daysZero, 0.0)
    }

    @Test
    fun testTankFillPercent() {
        val percent = WaterCalculator.tankFillPercent(500.0, 1000.0)
        assertEquals(50, percent)

        // Capacity is 0
        val percentZero = WaterCalculator.tankFillPercent(500.0, 0.0)
        assertEquals(0, percentZero)

        // Over max capacity (capped at 100)
        val percentOver = WaterCalculator.tankFillPercent(1500.0, 1000.0)
        assertEquals(100, percentOver)
    }

    @Test
    fun testImpactScore() {
        val score = WaterCalculator.impactScore(1000.0)
        assertEquals(50, score) // 1000 / 20 = 50

        // Cap at 100
        val maxScore = WaterCalculator.impactScore(5000.0)
        assertEquals(100, maxScore)
    }
}
