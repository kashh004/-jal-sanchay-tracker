package com.jalsanchay.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.jalsanchay.R
import com.jalsanchay.MainActivity
import com.jalsanchay.ui.onboarding.OnboardingActivity
import com.jalsanchay.ui.login.LoginActivity
import com.jalsanchay.databinding.ActivitySplashBinding
import com.jalsanchay.utils.PreferencesHelper

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        binding.splashLogo.startAnimation(fadeIn)
        binding.splashTitle.startAnimation(slideUp)

        binding.splashLogo.postDelayed({
            navigateNext()
        }, 2500)
    }

    private fun navigateNext() {
        val prefs = PreferencesHelper(this)
        
        val intent = when {
            prefs.isFirstLaunch() -> {
                // First time users go to Onboarding
                Intent(this, OnboardingActivity::class.java)
            }
            prefs.getUserId().isEmpty() -> {
                // Not first launch but not logged in
                Intent(this, LoginActivity::class.java)
            }
            else -> {
                // Logged in
                Intent(this, MainActivity::class.java)
            }
        }

        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
