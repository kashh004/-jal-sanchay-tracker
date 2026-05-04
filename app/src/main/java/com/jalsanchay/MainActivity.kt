package com.jalsanchay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jalsanchay.databinding.ActivityMainBinding
import com.jalsanchay.ui.ChatHelperBottomSheet
import com.jalsanchay.utils.WaterReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        
        binding.fabChat.setOnClickListener {
            val bottomSheet = ChatHelperBottomSheet()
            bottomSheet.show(supportFragmentManager, "ChatHelperBottomSheet")
        }
        
        setupDailyReminder()
    }

    private fun setupDailyReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(1, TimeUnit.DAYS)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyWaterReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }
}
