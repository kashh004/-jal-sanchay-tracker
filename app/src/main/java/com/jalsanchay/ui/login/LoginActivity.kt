package com.jalsanchay.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jalsanchay.MainActivity
import com.jalsanchay.databinding.ActivityLoginBinding
import com.jalsanchay.utils.PreferencesHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Credential list for 5 users
    private val users = mapOf(
        "akash@jalsanchay.com" to "pass123",
        "user1@jalsanchay.com" to "water001",
        "user2@jalsanchay.com" to "savewater",
        "user3@jalsanchay.com" to "rain2024",
        "user4@jalsanchay.com" to "nature01"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (users.containsKey(email) && users[email] == password) {
                    PreferencesHelper(this).setLoggedInUser(email)
                    Toast.makeText(this, "Welcome back, ${PreferencesHelper(this).getLoggedInUserDisplayName()}!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials. Try akash@jalsanchay.com / pass123", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, com.jalsanchay.ui.onboarding.OnboardingActivity::class.java))
            finish()
        }
    }
}
