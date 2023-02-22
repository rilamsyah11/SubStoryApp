package com.rizki.substoryapp.ui.main.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.rizki.substoryapp.R
import com.rizki.substoryapp.databinding.ActivityMapStoriesBinding
import com.rizki.substoryapp.helper.ViewsModelFactory
import com.rizki.substoryapp.preference.UsersPreference
import com.rizki.substoryapp.response.ListStory
import com.rizki.substoryapp.response.ResponseStory
import com.rizki.substoryapp.retrofit.ConfigApi
import com.rizki.substoryapp.ui.main.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.DataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapStoriesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapStoriesBinding
    private lateinit var mapStoriesViewModel: MainViewModel
    private lateinit var fusedLocation: FusedLocationProviderClient

    private val _storyLocation = MutableLiveData<List<ListStory>>()
    private val storyLocation: LiveData<List<ListStory>> = _storyLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.maps_stories)

        setViewModel()
        getLocationStory()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setViewModel() {
        mapStoriesViewModel = ViewModelProvider(
            this,
            ViewsModelFactory(UsersPreference.getInstances(DataStore))
        )[MainViewModel::class.java]
    }

    private fun getLocationStory() {
        mapStoriesViewModel.getUsers().observe(this) {
            if(it != null) {
                val client = ConfigApi.getServiceApi().getStoryWithLocation("Bearer " + it.Token, 1,40,1)
                client.enqueue(object : Callback<ResponseStory> {
                    override fun onResponse(
                        call: Call<ResponseStory>,
                        response: Response<ResponseStory>
                    ) {
                        val responsesBody = response.body()
                        if(response.isSuccessful && responsesBody?.Message == "Stories fetched successfully") {
                            _storyLocation.value = responsesBody.ListStory
                        }
                    }

                    override fun onFailure(call: Call<ResponseStory>, t: Throwable) {
                        Toast.makeText(this@MapStoriesActivity, getString(R.string.load_story_failed), Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val indonesia = LatLng(-2.3932797, 108.8507139)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))

        storyLocation.observe(this) {
            for(i in storyLocation.value?.indices!!) {
                val storyLatLng = LatLng(storyLocation.value?.get(i)?.Lat!!, storyLocation.value?.get(i)?.Lon!!)
                mMap.addMarker(
                    MarkerOptions()
                        .position(storyLatLng)
                        .title(getString(R.string.story_upload) + storyLocation.value?.get(i)?.Name)
                        .snippet(getString(R.string.story_desc) + storyLocation.value?.get(i)?.Description)
                )
            }
        }
        getMyLastLocation()
        styleMaps()
    }

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showFindMyMarkerLocation(location: Location) {
        lat = location.latitude
        lng = location.longitude

        val startMyLocation = LatLng(lat, lng)
        mMap.addMarker(
            MarkerOptions()
                .position(startMyLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .draggable(true)
                .title(getString(R.string.you_location))
        )
    }

    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    mMap.isMyLocationEnabled = true
                    showFindMyMarkerLocation(location)
                } else {
                    Toast.makeText(
                        this@MapStoriesActivity,
                        getString(R.string.not_found_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.type_normal -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.type_satellite -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.type_terrain -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.type_hybrid -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun styleMaps() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_maps))
            if (!success) {
                Log.e(TAGS, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAGS, "Can't find style. Error: ", exception)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val TAGS = "Map Stories Activity"
        var lat = 0.0
        var lng = 0.0
    }

}