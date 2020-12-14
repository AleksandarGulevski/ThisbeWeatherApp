package com.denofdevelopers.thisbeweatherapp.screen.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.denofdevelopers.thisbeweatherapp.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    companion object{
        fun start(context: Context){
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }
}