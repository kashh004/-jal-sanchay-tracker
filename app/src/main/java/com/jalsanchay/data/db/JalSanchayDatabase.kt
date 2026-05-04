package com.jalsanchay.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jalsanchay.data.models.RainfallEntry

@Database(entities = [RainfallEntry::class], version = 1, exportSchema = false)
abstract class JalSanchayDatabase : RoomDatabase() {
    abstract fun rainfallDao(): RainfallDao

    companion object {
        @Volatile private var INSTANCE: JalSanchayDatabase? = null

        fun getDatabase(context: Context): JalSanchayDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    JalSanchayDatabase::class.java,
                    "jalsanchay_db"
                ).build().also { INSTANCE = it }
            }
    }
}
