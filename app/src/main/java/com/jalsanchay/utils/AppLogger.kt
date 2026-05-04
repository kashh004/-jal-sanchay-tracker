package com.jalsanchay.utils

import android.util.Log
import java.io.File

/**
 * Centralized logging for debugging and error tracking
 */
object AppLogger {
    private const val TAG = "JalSanchay"
    private var logFile: File? = null
    
    fun init(logDirectory: File) {
        logFile = File(logDirectory, "jalsanchay_logs.txt")
    }
    
    fun d(message: String, throwable: Throwable? = null) {
        Log.d(TAG, message, throwable)
        writeToFile("DEBUG", message, throwable)
    }
    
    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
        writeToFile("ERROR", message, throwable)
    }
    
    fun w(message: String, throwable: Throwable? = null) {
        Log.w(TAG, message, throwable)
        writeToFile("WARN", message, throwable)
    }
    
    fun i(message: String) {
        Log.i(TAG, message)
        writeToFile("INFO", message, null)
    }
    
    private fun writeToFile(level: String, message: String, throwable: Throwable?) {
        try {
            val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            val logMessage = "[$timestamp] [$level] $message\n"
            
            logFile?.appendText(logMessage)
            
            if (throwable != null) {
                logFile?.appendText(throwable.stackTraceToString() + "\n\n")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error writing to log file", e)
        }
    }
    
    fun getLogs(): String {
        return try {
            logFile?.readText() ?: "No logs available"
        } catch (e: Exception) {
            "Error reading logs: ${e.message}"
        }
    }
    
    fun clearLogs() {
        try {
            logFile?.writeText("")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing logs", e)
        }
    }
}

/**
 * Exception handler for uncaught exceptions
 */
class GlobalExceptionHandler(private val defaultHandler: Thread.UncaughtExceptionHandler?) : 
    Thread.UncaughtExceptionHandler {
    
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        AppLogger.e("UNCAUGHT EXCEPTION in thread: ${thread.name}", throwable)
        defaultHandler?.uncaughtException(thread, throwable)
    }
}
