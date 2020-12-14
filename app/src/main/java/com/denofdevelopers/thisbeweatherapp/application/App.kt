package com.denofdevelopers.thisbeweatherapp.application

import android.app.Application
import com.denofdevelopers.thisbeweatherapp.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import timber.log.Timber

class App: Application() {

    private var appComponent: AppComponent? = null

    companion object {
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initAppComponent()
        initTimber()
        initCalligraphyConfig()
    }


    private fun initTimber(){
        Timber.plant(Timber.DebugTree())
    }

    private fun initCalligraphyConfig() {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }

    fun getAppComponent(): AppComponent? {
        return appComponent
    }

    private fun initAppComponent(){
        appComponent = DaggerAppComponent.builder().build()
    }
}