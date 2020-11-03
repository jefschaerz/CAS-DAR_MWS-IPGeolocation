package com.example.ipgeolocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


/* Simulate API response
"status": "success",
"country": "Royaume-Uni",
"countryCode": "GB",
"region": "NIR",
"regionName": "Irlande du Nord",
"city": "Craigavon",
"zip": "BT66",
"lat": 54.4471,
"lon": -6.387,
"timezone": "Europe/London",
"isp": "Virgin Media",
"org": "",
"as": "AS5089 Virgin Media Limited",
"query": "82.15.65.85" */
data class IPLocationData(val query : String,
                          val status: String,
                          val country: String,
                            val city : String)

class MainActivity : AppCompatActivity()  {
    private val TAG = MainActivity::class.java.simpleName
    private var textView: TextView? = null
    // Define fake Data :
    val ipLocationData1 = IPLocationData("82.15.68.85", "success","Royaume-Uni", "Craignon")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Log :
        Log.i(TAG,"dans onCreate")

        // Define components to use them
        // either use  :
        //        val button: Button = findViewById(R.id.buttonSearch)
        // or direct if import kotlinx.... defined

        // Define object to use
        buttonSearch.setOnClickListener {
            Log.d(TAG, "onClick: called")

            // Display Results values
            textViewResults.text="Results for IP: " + ipLocationData1.query.toString()
            textViewCountryValue.text=(ipLocationData1.country.toString())
            textViewCityValue.text=(ipLocationData1.city.toString())
        }
    }

    override fun onStart() {
        Log.i(TAG, "dans onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.i(TAG, "dans onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.i(TAG, "dans onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.i(TAG, "dans onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.i(TAG, "dans onDestroy")
        super.onDestroy()
    }

    // Permet de sauver pour récupérer ensuite avec le restore
    // Sera appelé entre on Pause et on Stop
    override fun onSaveInstanceState(savedInstanceState : Bundle) {
        // Le Bundle est une class (court terme) qui a des méthodes pour sauver
       Log.i(TAG, "onSavedInstanceState called")
        super.onSaveInstanceState(savedInstanceState)
    }

    // Permet de récupérer après
    // Sera appelé entre onStart et onResume
    override fun onRestoreInstanceState(savedInstanceState : Bundle) {
        //Essaye de récupérer un compteur et voir s'il est conservé après le on stop..
        Log.i(TAG, "onRestoreInstanceState")
         super.onRestoreInstanceState(savedInstanceState)
        //val savedString = savedInstanceState.getString(TEXT_CONTENTS, "")

    }
}