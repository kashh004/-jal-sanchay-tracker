package com.jalsanchay.utils

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

/**
 * Weather API Service Interface
 * Uses Open-Meteo Weather API (free, no key required)
 * Alternative: Use WeatherAPI.com or OpenWeatherMap for more features
 */
interface WeatherApiService {
    @GET("v1/forecast")
    fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "precipitation_sum",
        @Query("temperature_unit") unit: String = "celsius",
        @Query("timezone") timezone: String = "auto"
    ): Call<WeatherResponse>
}

/**
 * Data classes for weather API response
 */
data class WeatherResponse(
    val daily: DailyWeather
)

data class DailyWeather(
    val time: List<String>,
    @SerializedName("precipitation_sum")
    val precipitationSum: List<Float?>
)

data class WeatherData(
    val date: String,
    val rainfallMm: Float
)

/**
 * Weather Manager for Auto Rainfall Fetching
 * Features:
 * - Fetch rainfall data from weather API
 * - Auto-fill rainfall entries
 * - Support multiple weather providers
 * - Location-based rainfall tracking
 */
class WeatherManager(private val context: Context) {
    
    companion object {
        private const val OPEN_METEO_BASE_URL = "https://archive-api.open-meteo.com/"
        private const val WEATHER_API_BASE_URL = "https://api.weatherapi.com/v1/"
        private const val OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/3.0/"
    }
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(OPEN_METEO_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val weatherService = retrofit.create(WeatherApiService::class.java)
    
    /**
     * Fetch rainfall data for a location (no API key needed for Open-Meteo)
     */
    fun fetchRainfallData(
        latitude: Double,
        longitude: Double,
        onSuccess: (List<WeatherData>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            weatherService.getWeatherForecast(latitude, longitude)
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        if (response.isSuccessful) {
                            val weatherData = response.body()?.let { parseWeatherData(it) } ?: emptyList()
                            onSuccess(weatherData)
                            AppLogger.d("Weather data fetched successfully: ${weatherData.size} days")
                        } else {
                            onError("API error: ${response.code()}")
                            AppLogger.e("Weather API error: ${response.code()}")
                        }
                    }
                    
                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        onError(t.message ?: "Unknown error")
                        AppLogger.e("Weather API failure: ${t.message}")
                    }
                })
        } catch (e: Exception) {
            onError(e.message ?: "Unknown error")
            AppLogger.e("Weather fetch error: ${e.message}")
        }
    }
    
    /**
     * Parse weather API response into usable rainfall data
     */
    private fun parseWeatherData(response: WeatherResponse): List<WeatherData> {
        return response.daily.time.zip(response.daily.precipitationSum).mapNotNull { (date, rainfall) ->
            rainfall?.let {
                WeatherData(date, it)
            }
        }
    }
    
    /**
     * Get rainfall for today (for quick auto-fill)
     */
    fun getTodayRainfall(
        latitude: Double,
        longitude: Double,
        onSuccess: (Float) -> Unit,
        onError: (String) -> Unit
    ) {
        fetchRainfallData(latitude, longitude,
            { weatherDataList ->
                if (weatherDataList.isNotEmpty()) {
                    onSuccess(weatherDataList.first().rainfallMm)
                } else {
                    onError("No rainfall data available")
                }
            },
            { error ->
                onError(error)
            }
        )
    }
    
    /**
     * Auto-fill rainfall entry for a specific date
     */
    fun autoFillRainfallEntry(
        latitude: Double,
        longitude: Double,
        date: String,
        roofAreaM2: Float,
        runoffCoefficient: Float,
        onSuccess: (Double) -> Unit,
        onError: (String) -> Unit
    ) {
        fetchRainfallData(latitude, longitude,
            { weatherDataList ->
                val matchingData = weatherDataList.find { it.date == date }
                if (matchingData != null) {
                    val litersHarvested = WaterCalculator.calculateLiters(
                        matchingData.rainfallMm.toDouble(),
                        roofAreaM2.toDouble(),
                        runoffCoefficient.toDouble()
                    )
                    onSuccess(litersHarvested)
                    AppLogger.d("Auto-filled rainfall: ${matchingData.rainfallMm}mm = $litersHarvested L")
                } else {
                    onError("No rainfall data for date: $date")
                }
            },
            { error ->
                onError(error)
            }
        )
    }
    
    /**
     * Get 7-day rainfall forecast
     */
    fun get7DayForecast(
        latitude: Double,
        longitude: Double,
        onSuccess: (List<WeatherData>) -> Unit,
        onError: (String) -> Unit
    ) {
        fetchRainfallData(latitude, longitude,
            { weatherDataList ->
                val sevenDays = weatherDataList.take(7)
                onSuccess(sevenDays)
                AppLogger.d("7-day forecast fetched: $sevenDays")
            },
            { error ->
                onError(error)
            }
        )
    }
    
    /**
     * Get monthly rainfall summary
     */
    fun getMonthlyRainfallSummary(
        latitude: Double,
        longitude: Double,
        month: String, // Format: "2026-04"
        onSuccess: (Float) -> Unit,
        onError: (String) -> Unit
    ) {
        fetchRainfallData(latitude, longitude,
            { weatherDataList ->
                val monthlyData = weatherDataList.filter { it.date.startsWith(month) }
                val totalRainfall = monthlyData.sumOf { it.rainfallMm.toDouble() }.toFloat()
                onSuccess(totalRainfall)
                AppLogger.d("Monthly rainfall ($month): $totalRainfall mm")
            },
            { error ->
                onError(error)
            }
        )
    }
}
