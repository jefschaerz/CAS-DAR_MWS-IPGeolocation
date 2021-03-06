package com.example.ipgeolocation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayList


/* Simulate API response
"status": "success",
"message" : "invalid query",
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

/* Result from API : https://ip-api.com/docs/api:json#test */

const val URL_API = "http://ip-api.com/json/"
const val SHAREDPREF = "SharedPrefs"
const val LASTIPUSED = "LastIPUsed"
const val LANGUAGE_API ="fr"
const val IP_ITEM_ = "Item_"
const val IP_ITEM_NB = "ItemsNb"
const val LASTIPUSED_DEFAULT = "87.88.89.90"

data class IPLocationDataLocal(
    val query: String,
    val status: String,
    val country: String,
    val city: String
)

class MainActivity : AppCompatActivity()  {
    private val TAG = MainActivity::class.java.simpleName
    private val RESULT_SELECTION = 0
    private var textView: TextView? = null
    private var ipLocationDataFromAPI : IPLocationData? = null
    private var listIPLocationInfos = ArrayList<IPLocationData>()
    public var languageAPI :String? = "fr"

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Define Application name displayed
        title = "IP Locator App by JFS"
        Log.i(TAG, "dans onCreate")

        // Setup interface (in fun ?)
        textViewResults.text = "" ;
        linearLayoutValues.setVisibility(View.INVISIBLE)

        // Define adapter for LocationInfos
        var locationInfosAdapter = LocationInfosAdapter(this, listIPLocationInfos)
        lvLocationInfos.adapter = locationInfosAdapter

        // Define Try to locate button OnClick action
        buttonSearch.setOnClickListener {
            Log.d(TAG, "Try to locate onClick: called")
            // Check if IP is valid
            val isValidIP = IPAddressValidation.isValidIPAddress(editTextIPAddress.text.toString()) ;
            Log.d(TAG, "IP :" + editTextIPAddress.text.toString())
            if (!isValidIP)
                // IP NOT Valid --> Inform user by Toast
                Toast.makeText(
                    this,
                    String.format(getString(R.string.messageprovidedIPnotvalid)),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                // IP valid --> Ask the API
                // Clear previous attempt
                textViewResults.text = "Searching......"
                listIPLocationInfos.clear();
                val askAPIData = ContactAPI()
                try {
                    val url = URL_API
                    val chosenIP = editTextIPAddress.text.toString()
                    val fields = "?fields=status,message,continent,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,query" + "&lang=" + languageAPI
                    Log.d(TAG, "Start call to API with URL: " + url + chosenIP + fields)
                    askAPIData.execute(url + chosenIP + fields)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Define Use from a List OnClick action
        buttonUseFromList.setOnClickListener {
            Log.d(TAG, "From list button onClick: called")
            // Create intent for List activity
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("MainIpAddress", editTextIPAddress.text.toString())
            // Start activity and wait for result
            startActivityForResult(intent, RESULT_SELECTION);
        }

        // Define See on Map OnClick action
        buttonMap.setOnClickListener {
            var LngToUse: String? = "0"
            var LatToUse: String? = "0"
            var CityToUse: String? = "Unknown"

            Log.d(TAG, "Use see on map button onClick: called")
            // Create intent for List activity
            val intent = Intent(this, MapsActivity::class.java)

            // Search for lat and lng in listIPLocationInfos
            listIPLocationInfos!!.forEach { value ->
                if (value.title == "lon") {
                    LngToUse = value.content
                }
                if (value.title == "lat") {
                    LatToUse = value.content
                }
                if (value.title == "city") {
                    CityToUse = value.content
                }
            }
            intent.putExtra("CurrentLocation", CityToUse)
            intent.putExtra("Lat", LatToUse)
            intent.putExtra("Lng", LngToUse)
            startActivity(intent);
        }

        // Load data from SharedPreferences
        loadData()
    }
    // Add menu for Options
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options, menu)
        return true
    }

    // Actions on click menu items for API Language
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_langfr -> {
            Toast.makeText(this, "FR language selected for API", Toast.LENGTH_LONG).show()
            languageAPI = "fr"
            true
        }
        R.id.action_langen -> {
            Toast.makeText(this, "EN language selected for API", Toast.LENGTH_LONG).show()
            languageAPI = "en"
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


    // ******************************************
    /* Load and Save Data in SharedPreferences */
    // ******************************************
    private fun loadData(){
        // Get handle to shared preferences
        val sharedPref = this?.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
        val defaultValue = LASTIPUSED_DEFAULT
        val defaultValue_lang = "en"
        Log.i(TAG, "Value default : " + defaultValue)
        // Retrieve last IPAddress
        editTextIPAddress.setText(
            sharedPref.getString(LASTIPUSED,
                defaultValue
            )
        )
        languageAPI = sharedPref.getString(LANGUAGE_API, defaultValue_lang)

        // Use GSON for list of IP
        Log.i(TAG, "Value : " + editTextIPAddress.text.toString())
    }

    private fun saveData(){
        // Get handle to shared preferences
        val sharedPref = this?.getSharedPreferences(
            SHAREDPREF, Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putString(LASTIPUSED, editTextIPAddress.text.toString())
            putString(LANGUAGE_API, languageAPI)
            // List of IP address defined
            apply()
        }
    }
    // ******************************************
    /* Manage adapter for the list of API info */
    // ******************************************
    inner class LocationInfosAdapter : BaseAdapter {

        private var ipLocationInfosList = ArrayList<IPLocationData>()
        private var context: Context? = null

        constructor(context: Context, notesList: ArrayList<IPLocationData>) : super() {
            this.ipLocationInfosList = notesList
            this.context = context
        }

        // Necessary to redefine methods
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                // Link the according view to the holder
                view = layoutInflater.inflate(R.layout.locationinfos, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                // Log.i("TAG", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvTitle.text = (ipLocationInfosList[position].title)?.capitalize()
            vh.tvContent.text = ipLocationInfosList[position].content

            return view
        }

        override fun getItem(position: Int): Any {
            return ipLocationInfosList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return ipLocationInfosList.size
        }
    }

    private class ViewHolder(view: View?) {
        val tvTitle: TextView
        val tvContent: TextView

        init {
            this.tvTitle = view?.findViewById(R.id.textViewTitle) as TextView
            this.tvContent = view?.findViewById(R.id.textViewValue) as TextView
        }
    }
    // ******************************************
    /* Async Task for API contact */
    // ******************************************
    inner class ContactAPI : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String?{

            var result = ""
            var url: URL
            val httpURLConnection: HttpURLConnection

            try {
                // Get info passed as parameter (API URL and IP)
                url = URL(p0[0])
                httpURLConnection = url.openConnection() as HttpURLConnection
                if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val inputStreamReader = InputStreamReader(inputStream)

                    var data = inputStreamReader.read()

                    while (data > 0) {
                        val character = data.toChar()
                        result += character
                        data = inputStreamReader.read()
                    }
                    return result
                }
                else {
                    println("ERROR ${httpURLConnection.responseCode}")
                }
                return null

            } catch (e: Exception) {
                e.printStackTrace()
                return result
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
               try {
                   // Create JSON object with the result to parse it later and extract data
                   val jSONObject = JSONObject(result)
                   println(jSONObject)
                   val status = jSONObject.getString("status")
                   val query = jSONObject.getString("query")
                   if (status == "success"){
                       // SHOW display panel
                       linearLayoutValues.setVisibility(View.VISIBLE)
                       // Parse values received
                       val query = jSONObject.getString("query")
                       // Update label for results
                       textViewResults.text =  "Results "+"(" + status + ")" + " for : " + query
                       val continent = jSONObject.getString("continent")
                       val country = jSONObject.getString("country")
                       val countryCode = jSONObject.getString("countryCode")
                       val regionName = jSONObject.getString("regionName")
                       val city = jSONObject.getString("city")
                       val zip = jSONObject.getString("zip")
                       val lat = jSONObject.getString("lat")
                       val lon = jSONObject.getString("lon")
                       val timezone = jSONObject.getString("timezone")
                       val isp = jSONObject.getString("isp")

                       // Set object with values
                       Log.d(TAG, "Set listIPLocationInfos.. ")
                       listIPLocationInfos.add(IPLocationData(1, "query", query))
                       listIPLocationInfos.add(IPLocationData(2, "continent", continent))
                       listIPLocationInfos.add(IPLocationData(2, "country", country))
                       listIPLocationInfos.add(IPLocationData(3, "countryCode", countryCode))
                       listIPLocationInfos.add(IPLocationData(5, "regionName", regionName))
                       listIPLocationInfos.add(IPLocationData(6, "city", city))
                       listIPLocationInfos.add(IPLocationData(7, "zip", zip))
                       listIPLocationInfos.add(IPLocationData(8, "lat", lat))
                       listIPLocationInfos.add(IPLocationData(9, "lon", lon))
                       listIPLocationInfos.add(IPLocationData(10, "timezone", timezone))
                       listIPLocationInfos.add(IPLocationData(11, "isp", isp))

                       Log.d(
                           TAG,
                           "Number in listIPLocationInfos AFTER : " + (listIPLocationInfos.count()).toString()
                       )
                   }

                       else {
                       /* Failed */
                       // Hide Values Header panel
                       linearLayoutValues.setVisibility(View.INVISIBLE)

                       val message = jSONObject.getString("message")
                       textViewResults.text =  "Error (" + message + ") " + "for : " + query
                       Log.d(TAG, "FAILED : " + message + " Query : " + query)
                       }

                   // Update listview info by the adapter in all case
                   (lvLocationInfos.adapter as BaseAdapter).notifyDataSetChanged()

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
                // Do not change previous values
            }
        }
    }

    // ******************************************
    /* Manage LifeCyle of Application */
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
        // Save to SharedPreferences
        saveData()
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
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        // Le Bundle est une class (court terme) qui a des méthodes pour sauver
       Log.i(TAG, "onSavedInstanceState called")
        super.onSaveInstanceState(savedInstanceState)
        // TODO : Save IP address written in the field
    }

    // Permet de récupérer après
    // Sera appelé entre onStart et onResume
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Essaye de récupérer un compteur et voir s'il est conservé après le on stop..
        Log.i(TAG, "onRestoreInstanceState")
         super.onRestoreInstanceState(savedInstanceState)
        // TODO : Restored IP address written in the field
        //val savedString = savedInstanceState.getString(TEXT_CONTENTS, "")

    }
}