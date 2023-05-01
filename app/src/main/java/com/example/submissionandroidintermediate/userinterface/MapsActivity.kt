package com.example.submissionandroidintermediate.userinterface

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.UserPreferences
import com.example.submissionandroidintermediate.database.ListStoryDetail
import com.example.submissionandroidintermediate.databinding.ActivityMapsBinding
import com.example.submissionandroidintermediate.helper.LocationConverter
import com.example.submissionandroidintermediate.viewmodel.DataStoreViewModel
import com.example.submissionandroidintermediate.viewmodel.MainViewModel
import com.example.submissionandroidintermediate.viewmodel.MainViewModelFactory
import com.example.submissionandroidintermediate.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val boundBuilder = LatLngBounds.Builder()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }
    private val pref by lazy {
        UserPreferences.getInstance(dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            mapsViewModel.getStories(it)
        }

        mapsViewModel.stories.observe(this) {
            if (it != null) {
                setMarker(it)
            }
        }

        mapsViewModel.message.observe(this) {
            if (it != "Stories fetched successfully") Toast.makeText(this, it, Toast.LENGTH_SHORT)
                .show()
        }

        mapsViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar4.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setMarker(data: List<ListStoryDetail>) {
        lateinit var locationZoom: LatLng
        data.forEach {
            if (it.lat != null && it.lon != null) {
                val latLng = LatLng(it.lat, it.lon)
                val address = LocationConverter.getStringAddress(latLng, this)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(it.name)
                        .snippet(address)
                )
                boundBuilder.include(latLng)
                marker?.tag = it

                locationZoom = latLng
            }
        }

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                locationZoom, 3f
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun setMapStyle(mapsType: String) {
        if (mapsType == "standard") {
            try {
                val success =
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            this,
                            R.raw.maps_standard
                        )
                    )
                if (!success) {
                    Log.e(TAG, "Style parsing failed.")
                }
            } catch (exception: Resources.NotFoundException) {
                Log.e(TAG, "Can't find style. Error: ", exception)
            }
        } else {
            try {
                val success =
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            this,
                            R.raw.map_style
                        )
                    )
                if (!success) {
                    Log.e(TAG, "Style parsing failed.")
                }
            } catch (exception: Resources.NotFoundException) {
                Log.e(TAG, "Can't find style. Error: ", exception)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.standardMaps -> {
                setMapStyle("standard")
                true
            }
            R.id.nightMaps -> {
                setMapStyle("night")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}