package com.jalsanchay.ui.onboarding.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.jalsanchay.databinding.FragmentOnboardingStep3Binding
import com.jalsanchay.utils.PreferencesHelper

class OnboardingStep3Fragment : Fragment() {
    private var _binding: FragmentOnboardingStep3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferencesHelper(requireContext())
        
        // Setup Roof Material Dropdown
        val materials = arrayOf("Concrete/RCC", "Metal Sheet", "Tiles", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, materials)
        binding.roofMaterialInput.setAdapter(adapter)

        // Pre-fill values
        binding.roofAreaInput.setText(prefs.roofAreaM2.toString())
        binding.tankCapacityInput.setText(prefs.tankCapacityLiters.toString())
        binding.dailyUsageInput.setText(prefs.dailyUsageLiters.toString())
        binding.runoffCoefficientInput.setText(String.format("%.2f", prefs.defaultRunoffCoefficient))
        binding.roofMaterialInput.setText(prefs.roofType, false)
        
        binding.roofMaterialInput.setOnItemClickListener { _, _, position, _ ->
            val selected = materials[position]
            val coeff = when(selected) {
                "Metal Sheet" -> 0.90
                "Tiles" -> 0.95
                else -> 0.85
            }
            binding.runoffCoefficientInput.setText(coeff.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = PreferencesHelper(requireContext())
        
        val roofArea = binding.roofAreaInput.text.toString().toDoubleOrNull() ?: prefs.roofAreaM2
        val tankCapacity = binding.tankCapacityInput.text.toString().toDoubleOrNull() ?: prefs.tankCapacityLiters
        val dailyUsage = binding.dailyUsageInput.text.toString().toDoubleOrNull() ?: prefs.dailyUsageLiters
        val runoffCoeff = binding.runoffCoefficientInput.text.toString().toDoubleOrNull() ?: prefs.defaultRunoffCoefficient
        val roofType = binding.roofMaterialInput.text.toString()

        prefs.roofAreaM2 = roofArea
        prefs.tankCapacityLiters = tankCapacity
        prefs.dailyUsageLiters = dailyUsage
        prefs.defaultRunoffCoefficient = runoffCoeff
        prefs.roofType = roofType
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
