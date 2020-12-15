package com.denofdevelopers.thisbeweatherapp.screen.home

import com.denofdevelopers.thisbeweatherapp.common.BaseActivityPresenter

interface HomeContract {

    interface View {
        fun showProgress()
        fun hideProgress()
        fun showMessage(message: String)
    }

    interface Presenter : BaseActivityPresenter {
        fun getWeatherByCity(city: String)
        fun getWeatherByDeviceLocation(latitude: Double, longitude: Double)
    }
}