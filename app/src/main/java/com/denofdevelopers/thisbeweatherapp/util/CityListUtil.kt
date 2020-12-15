package com.denofdevelopers.thisbeweatherapp.util

import android.content.Context
import com.denofdevelopers.thisbeweatherapp.application.App
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object CityListUtil {

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    fun getCityList(): List<String> {
        val cityList: List<String>
        val jsonFileString = getJsonDataFromAsset(App.instance.applicationContext, "cities.json")
        val gson = Gson()
        val listPersonType = object : TypeToken<List<String>>() {}.type
        cityList = gson.fromJson(jsonFileString, listPersonType)
        return cityList
    }
}