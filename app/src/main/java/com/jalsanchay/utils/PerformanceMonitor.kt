package com.jalsanchay.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import kotlin.system.measureTimeMillis

/**
 * Monitors app performance metrics
 */
class PerformanceMonitor(private val context: Context) {
    
    data class MemoryStats(
        val usedMemory: Long,
        val totalMemory: Long,
        val availableMemory: Long,
        val memoryPercentage: Float
    )
    
    data class PerformanceMetrics(
        val executionTime: Long,
        val memoryBefore: Long,
        val memoryAfter: Long,
        val memoryDelta: Long
    )
    
    fun getMemoryStats(): MemoryStats {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val totalMemory = runtime.maxMemory()
        val availableMemory = totalMemory - usedMemory
        val memoryPercentage = (usedMemory.toFloat() / totalMemory.toFloat()) * 100
        
        return MemoryStats(
            usedMemory = usedMemory,
            totalMemory = totalMemory,
            availableMemory = availableMemory,
            memoryPercentage = memoryPercentage
        )
    }
    
    fun isLowMemory(): Boolean {
        val memStats = getMemoryStats()
        return memStats.memoryPercentage > 80
    }
    
    fun getSystemMemoryInfo(): Pair<Long, Long> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return Pair(memInfo.totalMem, memInfo.availMem)
    }
    
    fun measureExecutionTime(block: () -> Unit): Long {
        return measureTimeMillis {
            block()
        }
    }
    
    fun measurePerformance(name: String, block: () -> Unit): PerformanceMetrics {
        val memBefore = Debug.getNativeHeapAllocatedSize()
        val time = measureTimeMillis {
            block()
        }
        val memAfter = Debug.getNativeHeapAllocatedSize()
        
        AppLogger.d("$name took ${time}ms (Memory delta: ${memAfter - memBefore})")
        
        return PerformanceMetrics(
            executionTime = time,
            memoryBefore = memBefore,
            memoryAfter = memAfter,
            memoryDelta = memAfter - memBefore
        )
    }
}
