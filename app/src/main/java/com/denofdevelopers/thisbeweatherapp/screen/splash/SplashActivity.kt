package com.denofdevelopers.thisbeweatherapp.screen.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.denofdevelopers.thisbeweatherapp.R
import com.denofdevelopers.thisbeweatherapp.screen.home.HomeActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startNextActivity()
    }

    private fun startNextActivity() {
        HomeActivity.start(this)
    }
}