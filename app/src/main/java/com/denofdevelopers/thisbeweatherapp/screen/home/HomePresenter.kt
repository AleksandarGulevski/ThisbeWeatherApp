package com.denofdevelopers.thisbeweatherapp.screen.home

import com.denofdevelopers.thisbeweatherapp.R
import com.denofdevelopers.thisbeweatherapp.common.Constants.API_KEY
import com.denofdevelopers.thisbeweatherapp.common.Constants.UNITS
import com.denofdevelopers.thisbeweatherapp.model.WeatherResponse
import com.denofdevelopers.thisbeweatherapp.network.ApiService
import com.denofdevelopers.thisbeweatherapp.util.NetworkUtil
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HomePresenter(
    private var activity: HomeActivity,
    private var apiService: ApiService
) : HomeContract.Presenter {

    private var request: Disposable? = null

    override fun onStart() {}

    override fun getWeatherByCity(city: String) {
        activity.showProgress()
        request = apiService.getWeatherByCity(city, API_KEY, UNITS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onGetWeatherSuccess, this::onError)
    }

    override fun getWeatherByDeviceLocation(latitude: Double, longitude: Double) {
        activity.showProgress()
        request = apiService.getWeatherByDeviceLocation(latitude, longitude, API_KEY, UNITS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onGetWeatherSuccess, this::onError)
    }

    private fun onGetWeatherSuccess(weatherResponse: WeatherResponse) {
        activity.displayWeather(weatherResponse)
        activity.hideProgress()
    }

    private fun onError(error: Throwable) {
        activity.hideProgress()
        if (!NetworkUtil.isInternetAvailable(activity)) {
            activity.showMessage(activity.getString(R.string.no_internet))
        } else if (error is HttpException && error.code() == 404) {
            activity.showNoDataView()
            activity.showMessage("The selected city is not found in our database")
        } else {
            activity.showMessage(activity.getString(R.string.generic_error))
            Timber.e(error, "Home presenter error")
        }
    }

    override fun onStop() {
        cancelRequest()
    }

    private fun cancelRequest() {
        request?.dispose()
    }
}