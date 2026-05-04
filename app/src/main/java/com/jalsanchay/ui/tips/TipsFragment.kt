package com.jalsanchay.ui.tips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jalsanchay.databinding.FragmentTipsBinding
import com.jalsanchay.ui.MainViewModel

class TipsFragment : Fragment() {
    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Smart Tip Logic
        val p = viewModel.prefs
        if (p.tankCapacityLiters < 1000) {
            binding.tvSmartTipTitle.text = "🌟 SMART TIP: Upgrade Tank"
            binding.tvSmartTipDesc.text = "Your tank is quite small (${p.tankCapacityLiters.toInt()}L) compared to your roof area. Consider a larger tank to save more water during heavy rain!"
            binding.tvSmartTipTitle.setTextColor(android.graphics.Color.parseColor("#BA68C8"))
        } else if (p.roofAreaM2 > 100) {
            binding.tvSmartTipTitle.text = "🌟 SMART TIP: Maximize Roof"
            binding.tvSmartTipDesc.text = "You have a large roof (${p.roofAreaM2.toInt()} m²). Ensure you have multiple downpipes to capture all the fast-flowing water!"
            binding.tvSmartTipTitle.setTextColor(android.graphics.Color.parseColor("#BA68C8"))
        } else if (p.isFarmerMode) {
            binding.tvSmartTipTitle.text = "🌟 SMART TIP: Crop Hydration"
            binding.tvSmartTipDesc.text = "Use drip irrigation to extend your saved ${p.tankCapacityLiters.toInt()}L of water through the dry weeks."
            binding.tvSmartTipTitle.setTextColor(android.graphics.Color.parseColor("#BA68C8"))
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
