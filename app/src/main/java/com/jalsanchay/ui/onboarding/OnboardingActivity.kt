package com.jalsanchay.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jalsanchay.MainActivity
import com.jalsanchay.databinding.ActivityOnboardingBinding
import com.jalsanchay.ui.onboarding.fragments.OnboardingStep1Fragment
import com.jalsanchay.ui.onboarding.fragments.OnboardingStep2Fragment
import com.jalsanchay.ui.onboarding.fragments.OnboardingStep3Fragment

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // No text, just dots
        }.attach()

        binding.nextButton.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem++
            } else {
                navigateToMain()
            }
        }

        binding.skipButton.setOnClickListener {
            navigateToMain()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.nextButton.text = if (position == adapter.itemCount - 1) "START SAVING WATER 💧" else "NEXT"
            }
        })
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private inner class OnboardingAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingStep1Fragment()
                1 -> OnboardingStep2Fragment()
                2 -> OnboardingStep3Fragment()
                else -> OnboardingStep1Fragment()
            }
        }
    }
}
