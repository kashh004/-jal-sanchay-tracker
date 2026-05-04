package com.jalsanchay.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jalsanchay.R
import com.jalsanchay.databinding.FragmentDashboardBinding
import com.jalsanchay.ui.MainViewModel
import com.jalsanchay.utils.WaterCalculator
import com.jalsanchay.data.WeatherService
import com.jalsanchay.data.AiService
import com.jalsanchay.data.models.GeminiRequest
import com.jalsanchay.data.models.Content
import com.jalsanchay.data.models.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import java.text.DecimalFormat
import java.util.Calendar
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val df = DecimalFormat("#,##0.#")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Greeting
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        binding.tvGreeting.text = when {
            hour < 12 -> "Good morning,"
            hour < 17 -> "Good afternoon,"
            else -> "Good evening,"
        }
        binding.tvUserName.text = "${viewModel.prefs.getLoggedInUserDisplayName()} 🌿"

        // Observe data
        viewModel.allEntries.observe(viewLifecycleOwner) { entries ->
            if (entries.isEmpty()) {
                binding.tvLitersToday.text = "0"
                binding.tvWaterDays.text = "0.0"
                binding.tvLastEntry.text = "No entries yet"
                binding.progressTank.progress = 0
                binding.tvTankPercent.text = "0%"
                return@observe
            }
            val latest = entries.first()
            val usage = viewModel.prefs.dailyUsageLiters
            val tank = viewModel.prefs.tankCapacityLiters

            binding.tvLitersToday.text = df.format(latest.litersHarvested)
            binding.tvWaterDays.text = df.format(WaterCalculator.householdWaterDays(latest.litersHarvested, usage))
            binding.tvLastEntry.text = "Last: ${latest.rainfallMm}mm on ${latest.date}"
            val pct = WaterCalculator.tankFillPercent(latest.litersHarvested, tank)
            ObjectAnimator.ofInt(binding.progressTank, "progress", 0, pct).apply {
                duration = 1200
                interpolator = DecelerateInterpolator()
                start()
            }
            binding.tvTankPercent.text = "$pct%"
        }

        viewModel.totalLiters.observe(viewLifecycleOwner) { total ->
            val t = total ?: 0.0
            val usage = viewModel.prefs.dailyUsageLiters
            val tank = viewModel.prefs.tankCapacityLiters
            binding.tvTotalSaved.text = df.format(t)
            binding.tvTotalDays.text = df.format(WaterCalculator.householdWaterDays(t, usage))
            binding.tvImpactScore.text = WaterCalculator.impactScore(t).toString()
            
            val goalPct = ((t / tank) * 100).toInt().coerceIn(0, 100)
            ObjectAnimator.ofInt(binding.progressGoal, "progress", 0, goalPct).apply {
                duration = 1200
                interpolator = DecelerateInterpolator()
                start()
            }
            binding.tvGoalLabel.text = "Goal: ${df.format(tank)} L"
            
            // Gamification Badge
            binding.tvUserName.text = "${viewModel.prefs.getLoggedInUserDisplayName()} • ${com.jalsanchay.utils.BadgeHelper.getBadgeForLiters(t)}"
            
            // Farmer Mode Logic
            if (viewModel.prefs.isFarmerMode) {
                binding.tvWaterDaysLabel.text = "CROP IRRIGATION"
            } else {
                binding.tvWaterDaysLabel.text = "HOUSEHOLD"
            }
            
            // Call AI
            fetchAiSuggestion(t, tank, viewModel.prefs.roofAreaM2)
        }

        viewModel.getMonthlyLiters().observe(viewLifecycleOwner) { monthly ->
            binding.tvMonthly.text = df.format(monthly ?: 0.0)
        }

        viewModel.entryCount.observe(viewLifecycleOwner) { count ->
            binding.tvEntryCount.text = count.toString()
        }

        binding.btnLogNow.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_log)
        }
        
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = WeatherService.api.getDailyPrecipitation()
                val todayRain = response.daily.precipitation_sum.firstOrNull() ?: 0.0
                withContext(Dispatchers.Main) {
                    binding.tvWeatherToday.text = "Today's rain (Bangalore): ${todayRain}mm"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvWeatherToday.text = "Weather unavailable"
                }
            }
        }
    }

    private fun fetchAiSuggestion(totalSaved: Double, tankSize: Double, roofArea: Double) {
        binding.tvAiSuggestion.text = "Analyzing your water savings pattern..."
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val prompt = "You are a water conservation AI expert. A user has a roof area of $roofArea sq meters, a tank of $tankSize Liters, and has saved $totalSaved Liters so far. Give a very short, one-sentence encouraging suggestion on how they can improve or what this means. Keep it under 15 words."
                
                val request = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                
                val response = AiService.api.generateContent(AiService.API_KEY, request)
                val suggestion = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                withContext(Dispatchers.Main) {
                    if (!suggestion.isNullOrEmpty()) {
                        binding.tvAiSuggestion.text = suggestion
                    } else {
                        binding.tvAiSuggestion.text = "Keep up the great work saving water!"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvAiSuggestion.text = "Based on your roof area, consider increasing storage before the monsoon."
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
