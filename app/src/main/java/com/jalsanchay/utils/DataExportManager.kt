package com.jalsanchay.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.jalsanchay.data.models.RainfallEntry
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Handles exporting rainfall data to CSV and text formats
 */
class DataExportManager(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    /**
     * Export data to CSV file
     */
    fun exportToCSV(entries: List<RainfallEntry>): Uri? {
        return try {
            val fileName = "JalSanchay_Report_${System.currentTimeMillis()}.csv"
            val csvFile = File(context.cacheDir, fileName)

            csvFile.bufferedWriter().use { writer ->
                // Write header
                writer.write("Date,Rainfall (mm),Roof Area (m²),Runoff Coefficient,Liters Harvested,Days of Supply\n")

                // Write data rows
                entries.forEach { entry ->
                    val daysOfSupply = entry.litersHarvested / PreferencesHelper(context).dailyUsageLiters
                    writer.write("${entry.date},${entry.rainfallMm},${entry.roofAreaM2},${entry.runoffCoefficient},${entry.litersHarvested},${String.format("%.2f", daysOfSupply)}\n")
                }
            }

            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", csvFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export data to text report
     */
    fun exportToTextReport(entries: List<RainfallEntry>, totalLiters: Double, co2Saved: Double): Uri? {
        return try {
            val fileName = "JalSanchay_Report_${System.currentTimeMillis()}.txt"
            val reportFile = File(context.cacheDir, fileName)
            val separator = "=".repeat(60)
            val divider = "-".repeat(60)

            reportFile.bufferedWriter().use { writer ->
                writer.write("$separator\n")
                writer.write("JalSanchay - Rainwater Harvesting Report\n")
                writer.write("$separator\n\n")

                writer.write("Report Generated: ${Date()}\n")
                writer.write("Total Entries: ${entries.size}\n")
                writer.write("Total Water Harvested: ${String.format("%.2f", totalLiters)} Liters\n")
                writer.write("CO₂ Offset: ${String.format("%.2f", co2Saved)} kg\n")
                writer.write("Trees Equivalent: ${String.format("%.2f", totalLiters * 0.0002)}\n\n")

                writer.write("$divider\n")
                writer.write("Detailed Entry Log\n")
                writer.write("$divider\n\n")

                entries.forEach { entry ->
                    val daysOfSupply = entry.litersHarvested / PreferencesHelper(context).dailyUsageLiters
                    writer.write("Date: ${entry.date}\n")
                    writer.write("Rainfall: ${entry.rainfallMm} mm\n")
                    writer.write("Water Harvested: ${String.format("%.2f", entry.litersHarvested)} L\n")
                    writer.write("Days of Supply: ${String.format("%.2f", daysOfSupply)}\n")
                    writer.write("${"-".repeat(30)}\n\n")
                }
            }

            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", reportFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Share exported file
     */
    fun shareFile(uri: Uri, fileName: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = when {
                fileName.endsWith(".csv") -> "text/csv"
                fileName.endsWith(".txt") -> "text/plain"
                fileName.endsWith(".pdf") -> "application/pdf"
                else -> "text/plain"
            }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
    }
}
