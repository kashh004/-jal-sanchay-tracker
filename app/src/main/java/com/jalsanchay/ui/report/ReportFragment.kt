package com.jalsanchay.ui.report

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.jalsanchay.databinding.FragmentReportBinding
import com.jalsanchay.ui.MainViewModel
import com.jalsanchay.utils.WaterCalculator
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val df = DecimalFormat("#,##0.#")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()

        viewModel.allEntries.observe(viewLifecycleOwner) { entries ->
            if (entries.isEmpty()) return@observe

            val total = entries.sumOf { it.litersHarvested }
            val usage = viewModel.prefs.dailyUsageLiters
            val impact = WaterCalculator.impactScore(total)

            binding.tvReportTotal.text = df.format(total)
            binding.tvReportDays.text = df.format(WaterCalculator.householdWaterDays(total, usage))
            binding.tvReportRainDays.text = entries.size.toString()
            binding.tvCo2.text = "${df.format(WaterCalculator.co2Saved(total))} kg"
            binding.tvTrees.text = df.format(WaterCalculator.treesEquivalent(total))
            binding.progressImpact.progress = impact
            binding.tvImpactScore.text = "$impact / 100"

            // Build chart
            val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outFmt = SimpleDateFormat("dd MMM", Locale.getDefault())
            val sorted = entries.sortedBy { it.date }
            val barEntries = sorted.mapIndexed { i, e -> BarEntry(i.toFloat(), e.litersHarvested.toFloat()) }
            val labels = sorted.map { e ->
                runCatching { outFmt.format(inFmt.parse(e.date)!!) }.getOrDefault(e.date)
            }

            val dataSet = BarDataSet(barEntries, "Litres Harvested").apply {
                color = Color.parseColor("#2196F3")
                valueTextColor = Color.parseColor("#1a3a5c")
                valueTextSize = 9f
                setDrawValues(entries.size <= 10)
            }
            binding.barChart.apply {
                data = BarData(dataSet).apply { barWidth = 0.6f }
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                notifyDataSetChanged()
                invalidate()
                animateY(600)
            }
        }

        viewModel.getMonthlyLiters().observe(viewLifecycleOwner) { monthly ->
            binding.tvReportMonthly.text = df.format(monthly ?: 0.0)
        }

        binding.btnExport.setOnClickListener {
            val currentEntries = viewModel.allEntries.value
            if (currentEntries.isNullOrEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(binding.root, "No data to export", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val file = File(requireContext().cacheDir, "JalSanchay_Report.csv")
                    val writer = CSVWriter(FileWriter(file))
                    writer.writeNext(arrayOf("Date", "Rainfall (mm)", "Roof Area (m2)", "Runoff Coefficient", "Liters Harvested"))
                    currentEntries.forEach { e ->
                        writer.writeNext(arrayOf(e.date, e.rainfallMm.toString(), e.roofAreaM2.toString(), e.runoffCoefficient.toString(), e.litersHarvested.toString()))
                    }
                    writer.close()
                    
                    val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_SUBJECT, "JalSanchay Water Savings Report")
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    withContext(Dispatchers.Main) {
                        startActivity(Intent.createChooser(intent, "Export Report"))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        com.google.android.material.snackbar.Snackbar.make(binding.root, "Export failed: ${e.message}", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupChart() {
        binding.barChart.apply {
            setNoDataText("No chart data available.")
            setNoDataTextColor(Color.parseColor("#1565C0"))
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(false)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor("#5a7a9a")
                textSize = 9f
                labelRotationAngle = -30f
            }
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E3EFF8")
                textColor = Color.parseColor("#5a7a9a")
                textSize = 9f
                axisMinimum = 0f
            }
            axisRight.isEnabled = false
            setExtraOffsets(8f, 8f, 8f, 16f)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
