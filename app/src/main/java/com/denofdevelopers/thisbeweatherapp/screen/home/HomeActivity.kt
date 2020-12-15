package com.denofdevelopers.thisbeweatherapp.screen.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import com.denofdevelopers.thisbeweatherapp.R
import com.denofdevelopers.thisbeweatherapp.application.App
import com.denofdevelopers.thisbeweatherapp.common.BaseActivity
import com.denofdevelopers.thisbeweatherapp.util.MessageUtil.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.text.DateFormat
import java.util.*

class HomeActivity : BaseActivity(), HomeContract.View {

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
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    private fun showDateAndTime() {
        dateTime.text = DateFormat.getDateTimeInstance().format(Date());
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
                        //TODO call presenter to get weather by longitude and latitude
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