package com.denofdevelopers.thisbeweatherapp.network

import com.denofdevelopers.thisbeweatherapp.model.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("APPID") API_KEY: String,
        @Query("units") UNITS: String
    ): Single<WeatherResponse>


    @GET("weather")
    fun getWeatherByDeviceLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("APPID") API_KEY: String,
        @Query("units") UNITS: String
    ): Single<WeatherResponse>
}