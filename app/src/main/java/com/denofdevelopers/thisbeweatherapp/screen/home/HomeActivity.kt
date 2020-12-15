package com.denofdevelopers.thisbeweatherapp.screen.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import com.denofdevelopers.thisbeweatherapp.R
import com.denofdevelopers.thisbeweatherapp.application.App
import com.denofdevelopers.thisbeweatherapp.common.BaseActivity
import com.denofdevelopers.thisbeweatherapp.model.WeatherResponse
import com.denofdevelopers.thisbeweatherapp.util.CityListUtil.getCityList
import com.denofdevelopers.thisbeweatherapp.util.InputViewHandler
import com.denofdevelopers.thisbeweatherapp.util.MessageUtil.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeContract.View {

    @Inject
    lateinit var presenter: HomePresenter

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var currentLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    private val REQUEST_CHECK_SETTINGS = 1
    private val REQUEST_GRANT_PERMISSION = 2
    private val LOCATION_REQUEST_INTERVAL = 10000
    private val LOCATION_REQUEST_FAST_INTERVAL = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        showDateAndTime()
        checkLocationPermission()
        createLocationRequest()
        settingsCheck()
        setAutoCompleteView()
        setupOnClickListeners()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    private fun showDateAndTime() {
        dateTime.text = DateFormat.getDateTimeInstance().format(Date());
    }

    private fun setupOnClickListeners() {
        getMyLocationButton.setOnClickListener {
            getCurrentLocation()
        }
    }

    private fun checkLocationPermission() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_GRANT_PERMISSION
            )
            return
        }
        if (locationCallback == null) {
            buildLocationCallback()
        }
        if (currentLocation == null) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest?.interval = LOCATION_REQUEST_INTERVAL.toLong()
        locationRequest?.fastestInterval = LOCATION_REQUEST_FAST_INTERVAL.toLong()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun settingsCheck() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task =
            client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            Timber.i("onSuccess: settingsCheck")
            getCurrentLocation()
        }
        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                Timber.i("onFailure: settingsCheck")
                try {
                    e.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.i(sendEx)
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this,
                OnSuccessListener<Location?> { location ->
                    Timber.d("onSuccess: getLastLocation")
                    if (location != null) {
                        currentLocation = location
                       presenter.getWeatherByDeviceLocation(location.latitude, location.longitude)
                        Timber.i("""Latitude is ${location.latitude} and longitude is ${location.longitude}""")
                    } else {
                        Timber.i("---> location is null")
                        buildLocationCallback()
                        hideProgress()
                    }
                })
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    currentLocation = location
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("---> onActivityResult: ")
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) getCurrentLocation()
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED) showMessage(
            "Please enable Location settings...!!!"
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_GRANT_PERMISSION) {
            getCurrentLocation()
        }
    }

    fun displayWeather(weatherResponse: WeatherResponse) {
        selectedCityName.text = checkForEmptyString(weatherResponse.name)
        currentTemperature.text = getString(
            R.string.celsius,
            checkForEmptyString(weatherResponse.main.temperature.toInt().toString())
        )
        val icon = weatherResponse.weather.map { it.icon }[0]
        Picasso.get()
            .load("http://openweathermap.org/img/wn/$icon@2x.png")
            .placeholder(R.drawable.loader_anim_small)
            .error(R.drawable.no_image)
            .into(weatherIcon)
        feelsLike.text = getString(
            R.string.feels_like,
            checkForEmptyString(weatherResponse.main.feelsLike.toInt().toString())
        )
        val description = weatherResponse.weather.map { it.description }[0]
        weatherDescription.text = checkForEmptyString(description)
        sunrise.text = getString(
            R.string.sunrise,
            checkForEmptyString(convertTimestampToFormattedTime(weatherResponse.sys.sunrise))
        )
        sunset.text = getString(
            R.string.sunset,
            checkForEmptyString(convertTimestampToFormattedTime(weatherResponse.sys.sunset))
        )
        tempMin.text = getString(
            R.string.temp_min,
            checkForEmptyString(weatherResponse.main.tempMin.toInt().toString())
        )
        tempMax.text = getString(
           R.string.temp_max,
            checkForEmptyString(weatherResponse.main.tempMax.toInt().toString())
        )
        humidity.text = getString(
            R.string.humidity,
            checkForEmptyString(weatherResponse.main.humidity.toString())
        )
        pressure.text = getString(
            R.string.pressure,
            checkForEmptyString(weatherResponse.main.pressure.toString())
        )
        visibility.text =
            getString(
                R.string.visibility,
                checkForEmptyString(weatherResponse.visibility)
            )
        windSpeed.text = getString(
            R.string.wind_speed,
            checkForEmptyString(weatherResponse.wind.speed.toString())
        )
    }

    private fun checkForEmptyString(string: String): String {
        return if (!TextUtils.isEmpty(string)) {
            string
        } else "No data"
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertTimestampToFormattedTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000L
        val sdf = SimpleDateFormat("HH:mm:ss")
        val date = Date(timestamp * 1000)
        return sdf.format(date)
    }

    private fun setAutoCompleteView() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.custom_simple_spinner_dropdown_item,
            getCityList()
        )
        searchCity.setAdapter(adapter)
        searchCity.threshold = 1
        searchCity.setTextColor(Color.BLACK)
        searchCity.setOnItemClickListener { adapterView, v, _, _ ->
            if (searchCity.text.toString() != null) {
                InputViewHandler.hideKeyboard(adapterView)
                presenter.getWeatherByCity(searchCity.text.toString())
            }
            searchCity.setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (searchCity.text.toString() != null) {
                        InputViewHandler.hideKeyboard(view)
                        presenter.getWeatherByCity(searchCity.text.toString())
                    }
                } else {
                    toast("Something went wrong, please try again")
                }
                true
            }
            false
        }
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