package com.denofdevelopers.thisbeweatherapp.screen.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.denofdevelopers.thisbeweatherapp.R
import com.denofdevelopers.thisbeweatherapp.application.App
import com.denofdevelopers.thisbeweatherapp.common.BaseActivity
import com.denofdevelopers.thisbeweatherapp.util.MessageUtil.toast
import kotlinx.android.synthetic.main.activity_home.*
import java.text.DateFormat
import java.util.*

class HomeActivity : BaseActivity(), HomeContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        showDateAndTime()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    private fun showDateAndTime() {
        dateTime.text = DateFormat.getDateTimeInstance().format(Date());
    }

    override fun showProgress() {
        progressSpinner.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressSpinner.visibility = View.INVISIBLE
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun setupActivityComponent() {
        App.instance.getAppComponent()?.plus(HomeModule(this))?.inject(this)
    }
}