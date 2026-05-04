# 🌤️ Weather API Integration Guide

## Overview

JalSanchay supports multiple weather APIs for auto-filling rainfall data:
1. **Open-Meteo** (FREE, No API key) - Default
2. **WeatherAPI.com** (FREE tier with API key) - Alternative
3. **OpenWeatherMap** (FREE tier with API key) - Alternative

## Quick Setup (Open-Meteo - Recommended)

Open-Meteo is **completely free** and requires **no API key**!

### Step 1: No Setup Required!

The app is already configured to use Open-Meteo.

### Step 2: Get User Location

The app needs latitude/longitude. Add location permissions to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### Step 3: Request Location Permission (Runtime)

In your Activity/Fragment:

```kotlin
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {
    
    private companion object {
        const val LOCATION_PERMISSION_CODE = 100
    }
    
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            fetchWeatherData()
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchWeatherData()
            }
        }
    }
}
```

### Step 4: Get Location (Using FusedLocationProviderClient)

Add dependency in `build.gradle.kts`:
```kotlin
implementation("com.google.android.gms:play-services-location:21.0.1")
```

Get location:

```kotlin
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    
    private fun getLocation() {
        try {
            if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                            fetchWeatherData(latitude, longitude)
                        } else {
                            // Location not available
                            AppLogger.d("Location not available, using default coordinates")
                        }
                    }
                    .addOnFailureListener { e ->
                        AppLogger.e("Location error: ${e.message}")
                    }
            }
        } catch (e: Exception) {
            AppLogger.e("Get location exception: ${e.message}")
        }
    }
}
```

### Step 5: Fetch Weather Data

```kotlin
import com.jalsanchay.utils.WeatherManager

class LogFragment : Fragment() {
    
    private val weatherManager by lazy { WeatherManager(requireContext()) }
    
    private fun autoFillRainfall() {
        // User's location (from location manager)
        val latitude = 28.7041  // Example: Delhi
        val longitude = 77.1025
        
        // User's roof configuration
        val roofAreaM2 = PreferencesHelper.getRoofArea()
        val runoffCoefficient = PreferencesHelper.getRunoffCoefficient()
        val date = "2026-04-25"  // Date to fill
        
        weatherManager.autoFillRainfallEntry(
            latitude = latitude,
            longitude = longitude,
            date = date,
            roofAreaM2 = roofAreaM2,
            runoffCoefficient = runoffCoefficient,
            onSuccess = { litersHarvested ->
                // Update UI with harvested water
                updateRainfallUI(litersHarvested)
                AppLogger.d("Auto-filled: $litersHarvested liters")
            },
            onError = { error ->
                showError("Failed to fetch weather: $error")
                AppLogger.e("Weather error: $error")
            }
        )
    }
}
```

## Advanced Usage

### Get Today's Rainfall

```kotlin
weatherManager.getTodayRainfall(
    latitude = 28.7041,
    longitude = 77.1025,
    onSuccess = { rainfallMm ->
        AppLogger.d("Today's rainfall: ${rainfallMm}mm")
    },
    onError = { error ->
        AppLogger.e("Error: $error")
    }
)
```

### Get 7-Day Forecast

```kotlin
weatherManager.get7DayForecast(
    latitude = 28.7041,
    longitude = 77.1025,
    onSuccess = { weatherDataList ->
        weatherDataList.forEach { data ->
            AppLogger.d("${data.date}: ${data.rainfallMm}mm")
        }
    },
    onError = { error ->
        AppLogger.e("Error: $error")
    }
)
```

### Get Monthly Rainfall

```kotlin
weatherManager.getMonthlyRainfallSummary(
    latitude = 28.7041,
    longitude = 77.1025,
    month = "2026-04",
    onSuccess = { totalRainfallMm ->
        AppLogger.d("April rainfall: ${totalRainfallMm}mm")
    },
    onError = { error ->
        AppLogger.e("Error: $error")
    }
)
```

## Alternative: WeatherAPI.com

### Step 1: Get Free API Key

1. Go to [WeatherAPI.com](https://www.weatherapi.com/)
2. Sign up for free account
3. Copy API key from dashboard

### Step 2: Update PreferencesHelper

```kotlin
// In PreferencesHelper.kt
fun setWeatherAPIKey(key: String) {
    preferences.edit().putString("weather_api_key", key).apply()
}

fun getWeatherAPIKey(): String {
    return preferences.getString("weather_api_key", "") ?: ""
}
```

### Step 3: Update WeatherManager (Uncomment Alternative Code)

In `WeatherManager.kt`, uncomment the WeatherAPI section and update initialization.

## Alternative: OpenWeatherMap

### Step 1: Get Free API Key

1. Go to [OpenWeatherMap](https://openweathermap.org/api)
2. Sign up for free account
3. Copy API key from account page

### Step 2: Similar Setup as WeatherAPI.com

Uncomment the OpenWeatherMap section in `WeatherManager.kt`.

## Open-Meteo API Details

### Advantages
- ✅ **FREE** - No API key needed
- ✅ **No Rate Limiting** - Unlimited requests
- ✅ **Accurate** - Combines multiple data sources
- ✅ **Open Source** - Supports data privacy
- ✅ **Global Coverage** - Works worldwide

### Disadvantages
- ❌ **Archive Only** - Historical and current data
- ❌ **Limited Forecast** - 7-16 day forecast (vs 30+ days)
- ❌ **No Real-time Alerts** - No severe weather alerts

### Endpoints Available

```
Historical Weather:
  https://archive-api.open-meteo.com/v1/archive?latitude=XX&longitude=XX&...

Current & Forecast:
  https://api.open-meteo.com/v1/forecast?latitude=XX&longitude=XX&...
```

### Parameters Used

```
- latitude: User's latitude
- longitude: User's longitude
- daily=precipitation_sum: Daily rainfall total
- temperature_unit: celsius/fahrenheit
- timezone: auto (automatic detection)
```

## Location Testing

### Test with Different Locations

```kotlin
// Mumbai (Coastal, High Rainfall)
val mumbaiLat = 19.0760
val mumbaiLon = 72.8777

// Jaipur (Desert, Low Rainfall)
val jaipurLat = 26.9124
val jaipurLon = 75.7873

// Kolkata (Monsoon, Very High Rainfall)
val kolkataLat = 22.5726
val kolkataLon = 88.3639

// Bangalore (Moderate Rainfall)
val bangaloreLat = 12.9716
val bangaloreLon = 77.5946
```

## Troubleshooting

### Weather Data Not Fetching
- ✅ Check internet connection
- ✅ Verify latitude/longitude are valid
- ✅ Ensure date is in YYYY-MM-DD format
- ✅ Check network logs in Logcat

### Location Permission Denied
- ✅ Grant location permission in app settings
- ✅ Ensure app has background location access
- ✅ Check AndroidManifest.xml for permissions

### No Rainfall Data for Date
- ✅ Open-Meteo might not have data for future dates
- ✅ Use 7-day forecast for predictions
- ✅ Fall back to manual entry for missing dates

### High Battery Drain
- ✅ Limit API calls to once per day
- ✅ Cache results locally
- ✅ Use background jobs with WorkManager

## Battery-Efficient Implementation

### Batch Update Multiple Days

```kotlin
// Fetch once, fill multiple days
weatherManager.fetchRainfallData(
    latitude = 28.7041,
    longitude = 77.1025,
    onSuccess = { weatherDataList ->
        // Insert all days at once
        weatherDataList.forEach { data ->
            saveRainfallEntry(data)
        }
    },
    onError = { error ->
        AppLogger.e("Batch update error: $error")
    }
)
```

### Use WorkManager for Scheduled Updates

```kotlin
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class WeatherUpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        return try {
            val weatherManager = WeatherManager(applicationContext)
            // Fetch weather here
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Schedule daily weather update
fun scheduleWeatherUpdates(context: Context) {
    val weatherUpdateRequest = PeriodicWorkRequestBuilder<WeatherUpdateWorker>(
        1, TimeUnit.DAYS
    ).build()
    
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "weather_update",
        ExistingPeriodicWorkPolicy.KEEP,
        weatherUpdateRequest
    )
}
```

## Privacy & Data Handling

### Location Privacy
- ✅ Only store location when user consents
- ✅ Implement opt-in for location tracking
- ✅ Allow manual location entry as alternative

### Weather Data
- ✅ Cache locally to reduce API calls
- ✅ Respect user privacy settings
- ✅ Don't share raw location data

## Next Steps

1. ✅ Choose weather API (Open-Meteo recommended)
2. ✅ Add location permissions
3. ✅ Implement location retrieval
4. ✅ Integrate WeatherManager into LogFragment
5. ✅ Test with multiple locations
6. ✅ Add UI for auto-fill button
7. ✅ Implement caching for performance

## Useful Resources

- [Open-Meteo Docs](https://open-meteo.com/en/docs)
- [WeatherAPI Docs](https://www.weatherapi.com/docs/)
- [OpenWeatherMap Docs](https://openweathermap.org/api)
- [Android Location Services](https://developer.android.com/training/location)
- [Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)

---

Your weather API integration is ready! Users can now auto-fill rainfall data based on real weather data. 🌤️
