package com.jalsanchay.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jalsanchay.data.models.RainfallEntry

@Dao
interface RainfallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: RainfallEntry): Long

    @Delete
    suspend fun delete(entry: RainfallEntry)

    @Query("SELECT * FROM rainfall_entries ORDER BY timestamp DESC")
    fun getAllEntries(): LiveData<List<RainfallEntry>>

    @Query("SELECT * FROM rainfall_entries ORDER BY timestamp DESC")
    suspend fun getAllEntriesOnce(): List<RainfallEntry>

    @Query("SELECT SUM(litersHarvested) FROM rainfall_entries")
    fun getTotalLiters(): LiveData<Double?>

    @Query("SELECT SUM(litersHarvested) FROM rainfall_entries WHERE date LIKE :monthPrefix || '%'")
    fun getMonthlyLiters(monthPrefix: String): LiveData<Double?>

    @Query("SELECT * FROM rainfall_entries WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC")
    fun getEntriesForMonth(monthPrefix: String): LiveData<List<RainfallEntry>>

    @Query("SELECT COUNT(*) FROM rainfall_entries")
    fun getEntryCount(): LiveData<Int>
}
