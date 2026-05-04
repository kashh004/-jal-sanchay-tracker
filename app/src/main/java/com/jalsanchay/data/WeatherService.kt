package com.jalsanchay.data

import com.jalsanchay.data.models.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getDailyPrecipitation(
        @Query("latitude") lat: Double = 12.9716, // Default to Bangalore
        @Query("longitude") lon: Double = 77.5946,
        @Query("daily") daily: String = "precipitation_sum",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

object WeatherService {
    private const val BASE_URL = "https://api.open-meteo.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: OpenMeteoApi = retrofit.create(OpenMeteoApi::class.java)
}
