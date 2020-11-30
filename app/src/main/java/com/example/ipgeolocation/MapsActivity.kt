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
    private var receivedLocation = "Unknown"
    private var locationLatDouble = 47.0
    private var LocationLngDouble = -8.41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get Location and lat, long from Main activity
        receivedLocation = intent.getStringExtra("CurrentLocation").toString()
        //receivedLocation = LocationName.toString()
        // Receive as string to display
        locationLatDouble = intent.getStringExtra("Lat")!!.toDouble()
        LocationLngDouble = intent.getStringExtra("Lng")!!.toDouble()
        Log.d(TAG, "After : "+ receivedLocation + " - "+ locationLatDouble + " - " + LocationLngDouble)
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

        // Add a marker on camera and move the camera
        val currentloc = LatLng(locationLatDouble, LocationLngDouble)
        mMap.addMarker(MarkerOptions().position(currentloc).title(receivedLocation + " (Lat:" + locationLatDouble.toString() + " - Lng:" + LocationLngDouble.toString() + ")" ))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentloc))
        // Zoom of 5x
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentloc, 10f))
        // Add button to zoom in /out ?
    }
}