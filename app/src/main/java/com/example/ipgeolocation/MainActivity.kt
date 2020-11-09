package com.example.ipgeolocation

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


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
    private val RESULT_SELECTION = 0
    private var textView: TextView? = null
    // Define fake Data :
    val ipLocationData1 = IPLocationData("82.15.68.85", "success","Royaume-Uni", "Craignon")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Log :
        Log.i(TAG,"dans onCreate")

        // Define Search button OnClick action
        /*buttonSearch.setOnClickListener {
            Log.d(TAG, "Search button onClick: called")

            // Display Results values
            textViewResults.text="Results for IP: " + ipLocationData1.query.toString()
            textViewCountryValue.text=(ipLocationData1.country.toString())
            textViewCityValue.text=(ipLocationData1.city.toString())
        }
        */


        // Define Use from a List OnClick action
        buttonUseFromList.setOnClickListener {
            Log.d(TAG, "From list button onClick: called")

            // Create intent for List activity
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("MainIpAddress", editTextIPAddress.text.toString())
            // Start activity and wait for result
            startActivityForResult(intent, RESULT_SELECTION);
        }
    }

    // ******************************************
    /* Manage API by AsyncTask */
    // ******************************************
    fun callAPI (view: View){
        val askAPIData = ContactAPI()
        try {
            val url = "http://ip-api.com/json/"
            val chosenIP = editTextIPAddress.text.toString()
            Log.d(TAG, "Start call to API with" + chosenIP)
            askAPIData.execute(url+chosenIP)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    inner class ContactAPI : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String {

            var result = ""
            var url: URL
            val httpURLConnection: HttpURLConnection

            try {
                // Get info passed as parameter (AP UI URL and IP)
                url = URL(p0[0])
                httpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream = httpURLConnection.inputStream
                val inputStreamReader = InputStreamReader(inputStream)

                var data = inputStreamReader.read()

                while (data > 0) {
                    val character = data.toChar()
                    result += character
                    data = inputStreamReader.read()
                }

                return result
            } catch (e: Exception) {
                e.printStackTrace()
                return result
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {

                val jSONObject = JSONObject(result)
                println(jSONObject)
                val country = jSONObject.getString("country")
                println(country)

                textViewCountryValue.text = country

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ******************************************
    /* Manage Activities */
    // ******************************************
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode : " + requestCode)
        Log.d(TAG, "resultCode : " + resultCode)
        if (requestCode == RESULT_SELECTION) {
            if (resultCode == RESULT_OK) {
                val result = data!!.getStringExtra("result")
                Log.d(TAG, "Result received : " + result)
                editTextIPAddress.setText(result)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                editTextIPAddress.setText("undefined")
            }
        }
    }



    // ******************************************
    /* Manage LyfeCyle of Application */
    // ******************************************
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