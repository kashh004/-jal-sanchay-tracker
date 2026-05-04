package com.jalsanchay.data.models

data class WeatherResponse(
    val daily: DailyData
)

data class DailyData(
    val time: List<String>,
    val precipitation_sum: List<Double>
)
