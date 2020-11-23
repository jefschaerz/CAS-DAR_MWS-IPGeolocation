package com.example.ipgeolocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = MapsActivity::class.java.simpleName
    private lateinit var mMap: GoogleMap
    private lateinit var LocationName : String
    private var LocationLatDouble = 47.0
    private var LocationLngDouble = -8.41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get Location and lat, long
        LocationName = intent.getStringExtra("CurrentLocation").toString()
        // Receive as string to display
        val LocationLat = intent.getStringExtra("Lat")
        val LocationLng = intent.getStringExtra("Lng")
        Log.d(TAG, "Your Device IP Address: "+ LocationLat + " - " + LocationLng)
        if (LocationLat != null) {
            LocationLatDouble = LocationLat?.toDouble()
        }
        if (LocationLng != null) {
            LocationLngDouble = LocationLng?.toDouble()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val currentloc = LatLng(LocationLatDouble, LocationLngDouble)
        mMap.addMarker(MarkerOptions().position(currentloc).title(LocationName))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentloc))

        // Zoom of 5x
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentloc, 5f))
    }
}