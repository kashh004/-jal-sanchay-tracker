package com.jalsanchay.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.jalsanchay.databinding.FragmentSettingsBinding
import com.jalsanchay.ui.MainViewModel
import com.jalsanchay.ui.login.LoginActivity

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load current prefs
        val p = viewModel.prefs
        binding.tvAccountTitle.text = "Welcome back, ${p.getLoggedInUserDisplayName()}"
        binding.tvAccountEmail.text = if (p.getUserId().isNotEmpty()) p.getUserId() else "Demo mode"

        binding.etCfgArea.setText(p.roofAreaM2.toString())
        binding.etCfgTank.setText(p.tankCapacityLiters.toString())
        binding.etCfgUsage.setText(p.dailyUsageLiters.toString())
        binding.etCfgRunoff.setText(p.defaultRunoffCoefficient.toString())
        
        // Advanced Options
        binding.switchFarmerMode.isChecked = p.isFarmerMode
        val roofTypes = arrayOf("Concrete", "Metal", "Tile")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roofTypes)
        binding.spinnerRoofType.adapter = adapter
        binding.spinnerRoofType.setSelection(roofTypes.indexOf(p.roofType).takeIf { it >= 0 } ?: 0)
        
        binding.spinnerRoofType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = roofTypes[position]
                val coef = when(selected) { "Metal" -> "0.90"; "Tile" -> "0.95"; else -> "0.85" }
                binding.etCfgRunoff.setText(coef)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        updateSummary()

        binding.btnSaveSettings.setOnClickListener {
            val area = binding.etCfgArea.text.toString().toDoubleOrNull()
            val tank = binding.etCfgTank.text.toString().toDoubleOrNull()
            val usage = binding.etCfgUsage.text.toString().toDoubleOrNull()
            val runoff = binding.etCfgRunoff.text.toString().toDoubleOrNull()

            if (area == null || area <= 0) { binding.tilCfgArea.error = "Invalid area"; return@setOnClickListener }
            if (tank == null || tank <= 0) { binding.tilCfgTank.error = "Invalid capacity"; return@setOnClickListener }
            if (usage == null || usage <= 0) { binding.tilCfgUsage.error = "Invalid usage"; return@setOnClickListener }
            if (runoff == null || runoff !in 0.1..1.0) { binding.tilCfgRunoff.error = "Invalid coefficient (0.1–1.0)"; return@setOnClickListener }

            p.roofAreaM2 = area
            p.tankCapacityLiters = tank
            p.dailyUsageLiters = usage
            p.roofType = binding.spinnerRoofType.selectedItem.toString()
            p.isFarmerMode = binding.switchFarmerMode.isChecked
            p.defaultRunoffCoefficient = runoff // Apply manual override if any
            
            binding.tilCfgArea.error = null
            binding.tilCfgTank.error = null
            binding.tilCfgUsage.error = null
            binding.tilCfgRunoff.error = null
            updateSummary()
            Snackbar.make(binding.root, "✅ Settings saved!", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            val prefs = viewModel.prefs
            prefs.clearLoggedInUser()
            Snackbar.make(binding.root, "Logout successful", Snackbar.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun updateSummary() {
        val p = viewModel.prefs
        binding.tvSumArea.text = "${p.roofAreaM2} m²"
        binding.tvSumTank.text = "${p.tankCapacityLiters.toLong()} L"
        binding.tvSumUsage.text = "${p.dailyUsageLiters} L/day"
        binding.tvSumRunoff.text = "${p.defaultRunoffCoefficient}"
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
