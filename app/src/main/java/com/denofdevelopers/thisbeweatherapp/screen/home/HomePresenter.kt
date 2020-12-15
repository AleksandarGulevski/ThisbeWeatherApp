package com.denofdevelopers.thisbeweatherapp.screen.home

import com.denofdevelopers.thisbeweatherapp.network.ApiService

class HomePresenter(
    private var activity: HomeActivity,
    private var apiService: ApiService
): HomeContract.Presenter {

    override fun getWeatherByCity(city: String) {
        TODO("Not yet implemented")
    }

    override fun getWeatherByDeviceLocation(latitude: Double, longitude: Double) {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        TODO("Not yet implemented")
    }

}