package com.denofdevelopers.thisbeweatherapp.screen.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.denofdevelopers.thisbeweatherapp.R
import com.denofdevelopers.thisbeweatherapp.common.BaseActivity

class HomeActivity : BaseActivity(), HomeContract.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    override fun showProgress() {
        TODO("Not yet implemented")
    }

    override fun hideProgress() {
        TODO("Not yet implemented")
    }

    override fun showMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun setupActivityComponent() {
        TODO("Not yet implemented")
    }
}