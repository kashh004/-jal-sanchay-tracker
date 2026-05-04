package com.jalsanchay.ui.log

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jalsanchay.databinding.FragmentLogBinding
import com.jalsanchay.ui.MainViewModel
import com.jalsanchay.utils.WaterCalculator
import java.text.DecimalFormat

class LogFragment : Fragment() {
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val df = DecimalFormat("#,##0.#")
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-fill from prefs
        binding.etArea.setText(viewModel.prefs.roofAreaM2.toString())
        binding.etRunoff.setText(viewModel.prefs.defaultRunoffCoefficient.toString())

        // Live preview watcher
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updatePreview() }
        }
        binding.etRainfall.addTextChangedListener(watcher)
        binding.etArea.addTextChangedListener(watcher)
        binding.etRunoff.addTextChangedListener(watcher)

        // History RecyclerView
        adapter = HistoryAdapter { entry -> viewModel.deleteEntry(entry) }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        viewModel.allEntries.observe(viewLifecycleOwner) { entries ->
            adapter.submitList(entries)
            binding.tvEntryBadge.text = "${entries.size} entries"
        }

        binding.btnSave.setOnClickListener { saveEntry() }
        binding.btnClear.setOnClickListener { clearInputs() }
    }

    private fun updatePreview() {
        val r = binding.etRainfall.text.toString().toDoubleOrNull() ?: return
        val a = binding.etArea.text.toString().toDoubleOrNull() ?: return
        val c = binding.etRunoff.text.toString().toDoubleOrNull() ?: return
        if (r < 0 || a <= 0 || c <= 0) return

        val liters = WaterCalculator.calculateLiters(r, a, c)
        val usage = viewModel.prefs.dailyUsageLiters
        val tank = viewModel.prefs.tankCapacityLiters

        binding.tvPreviewLiters.text = df.format(liters)
        binding.tvPreviewDays.text = df.format(WaterCalculator.householdWaterDays(liters, usage))
        binding.tvPreviewPct.text = "${WaterCalculator.tankFillPercent(liters, tank)}%"
    }

    private fun saveEntry() {
        val rStr = binding.etRainfall.text.toString()
        val aStr = binding.etArea.text.toString()
        val cStr = binding.etRunoff.text.toString()

        // Validate - handle non-numeric inputs gracefully
        val r = rStr.toDoubleOrNull()
        val a = aStr.toDoubleOrNull()
        val c = cStr.toDoubleOrNull()

        binding.tilRainfall.error = null
        binding.tilArea.error = null
        binding.tilRunoff.error = null

        var valid = true
        if (r == null || r < 0 || r > 500) {
            binding.tilRainfall.error = "Enter valid rainfall (0–500 mm)"
            valid = false
        }
        if (a == null || a < 1 || a > 5000) {
            binding.tilArea.error = "Enter valid area (1–5000 m²)"
            valid = false
        }
        if (c == null || c < 0.1 || c > 1.0) {
            binding.tilRunoff.error = "Enter valid coefficient (0.1–1.0)"
            valid = false
        }
        if (!valid) return

        viewModel.saveEntry(r!!, a!!, c!!)
        Toast.makeText(requireContext(),
            "✅ Saved! ${df.format(WaterCalculator.calculateLiters(r, a, c))} L harvested",
            Toast.LENGTH_SHORT).show()
        clearInputs()
    }

    private fun clearInputs() {
        binding.etRainfall.text?.clear()
        binding.tilRainfall.error = null
        binding.tvPreviewLiters.text = "—"
        binding.tvPreviewDays.text = "—"
        binding.tvPreviewPct.text = "—"
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
