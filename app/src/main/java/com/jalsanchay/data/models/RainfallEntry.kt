package com.jalsanchay.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rainfall_entries")
data class RainfallEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,           // "2026-04-24"
    val rainfallMm: Double,
    val roofAreaM2: Double,
    val runoffCoefficient: Double,
    val litersHarvested: Double,
    val timestamp: Long = System.currentTimeMillis()
)
