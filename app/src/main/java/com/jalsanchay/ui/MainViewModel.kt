package com.jalsanchay.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jalsanchay.data.db.JalSanchayDatabase
import com.jalsanchay.data.models.RainfallEntry
import com.jalsanchay.utils.PreferencesHelper
import com.jalsanchay.utils.WaterCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = JalSanchayDatabase.getDatabase(application).rainfallDao()
    val prefs = PreferencesHelper(application)

    val allEntries: LiveData<List<RainfallEntry>> = dao.getAllEntries()
    val totalLiters: LiveData<Double?> = dao.getTotalLiters()
    val entryCount: LiveData<Int> = dao.getEntryCount()

    private val _livePreview = MutableLiveData<Double>(0.0)
    val livePreview: LiveData<Double> = _livePreview

    fun getCurrentMonthPrefix(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getMonthlyLiters(): LiveData<Double?> =
        dao.getMonthlyLiters(getCurrentMonthPrefix())

    fun getEntriesForMonth(monthPrefix: String): LiveData<List<RainfallEntry>> =
        dao.getEntriesForMonth(monthPrefix)

    fun updatePreview(rainfall: Double, area: Double, runoff: Double) {
        if (rainfall >= 0 && area > 0 && runoff > 0) {
            _livePreview.value = WaterCalculator.calculateLiters(rainfall, area, runoff)
        } else {
            _livePreview.value = 0.0
        }
    }

    fun saveEntry(rainfall: Double, area: Double, runoff: Double) {
        viewModelScope.launch {
            val liters = WaterCalculator.calculateLiters(rainfall, area, runoff)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            dao.insert(
                RainfallEntry(
                    date = today,
                    rainfallMm = rainfall,
                    roofAreaM2 = area,
                    runoffCoefficient = runoff,
                    litersHarvested = liters
                )
            )
        }
    }

    fun deleteEntry(entry: RainfallEntry) {
        viewModelScope.launch { dao.delete(entry) }
    }
}
