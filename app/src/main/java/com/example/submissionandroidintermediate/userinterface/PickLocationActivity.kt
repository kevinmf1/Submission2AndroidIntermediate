package com.example.submissionandroidintermediate.userinterface

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.databinding.ActivityPickLocationBinding
import com.example.submissionandroidintermediate.helper.LocationConverter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPickLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateButtonStyle(binding.myLocation, false)
        updateButtonStyle(binding.locationPick, false)
        setActions()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        getMyLastLocation()

        mMap.setOnMapClickListener {
            pickedPlace = it
            val markerOptions = MarkerOptions()
            markerOptions.position(it)

            markerOptions.title(LocationConverter.getStringAddress(it, this))
            mMap.clear()
            val location = CameraUpdateFactory.newLatLngZoom(
                it, 15f
            )
            mMap.animateCamera(location)
            mMap.addMarker(markerOptions)
            updateButtonStyle(binding.locationPick, true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLagLng = LatLng(
                        location.latitude,
                        location.longitude
                    )
                    updateButtonStyle(binding.myLocation, true)
                    mMap.isMyLocationEnabled = true
                    showStartMarker(location)
                } else {
                    updateButtonStyle(binding.myLocation, false)
                    Toast.makeText(
                        this@PickLocationActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                    mMap.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation(), DEFAULT_ZOOM)
                    )
                    mMap.isMyLocationEnabled = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    fun updateButtonStyle(button: Button, isEnabled: Boolean) {
        if (isEnabled) {
            // Mengaktifkan style buttonEnabled
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.white_500))
            button.setTextColor(ContextCompat.getColor(this, R.color.black))
            button.isEnabled = true
        } else {
            // Mengaktifkan style buttonDisabled
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonColor))
            button.setTextColor(ContextCompat.getColor(this, R.color.wordColor))
            button.isEnabled = false
        }
    }

    private fun setActions() {
        binding.myLocation.setOnClickListener {
            showAlertDialog(currentLagLng)
        }

        binding.locationPick.setOnClickListener {
            showAlertDialog(pickedPlace)
        }
    }

    private fun showAlertDialog(latlng: LatLng?) {
        val address = LocationConverter.getStringAddress(latlng, this)
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle("Gunakan lokasi ini?")
            .setMessage(address)
            .setPositiveButton("Iya") { _, _ ->
                returnLocationResult(address, latlng)
            }
            .setNegativeButton("Tidak") { _, _ ->
                alert.cancel()
            }
            .show()
    }

    private fun returnLocationResult(address: String, latlng: LatLng?) {
        val resultIntent = Intent()
        resultIntent.putExtra("address", address)
        resultIntent.putExtra("lat", latlng?.latitude)
        resultIntent.putExtra("lng", latlng?.longitude)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun defaultLocation() = LatLng(-34.0, 151.0)

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title(getString(R.string.start_point))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, DEFAULT_ZOOM))
    }

    companion object {
        var IRcounter = 1
        var currentLagLng: LatLng? = null
        var pickedPlace: LatLng? = null
        const val DEFAULT_ZOOM = 15.0f
    }
}