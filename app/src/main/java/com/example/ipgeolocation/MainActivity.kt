package com.example.ipgeolocation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

    // Define simulation Data :
    val ipLocationData1 = IPLocationDataLocal("82.15.68.85", "success", "Royaume-Uni", "Craignon")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "IP Locator App by JFS"
        // Log :
        Log.i(TAG, "dans onCreate")

        // Setup interface (in fun ?)
        textViewResults.text = "" ;

        // Define adapter for LocationInfos
        var locationInfosAdapter = LocationInfosAdapter(this, listIPLocationInfos)
        lvLocationInfos.adapter = locationInfosAdapter

        // Define Try to locate  button OnClick action
        buttonSearch.setOnClickListener {
            // Clear previous attempt
            textViewResults.text = "Searching......"
            listIPLocationInfos.clear();
            val askAPIData = ContactAPI()
            try {
                val url = "http://ip-api.com/json/"
                val chosenIP = editTextIPAddress.text.toString()
                val fields = "?fields=status,message,continent,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,query"
                Log.d(TAG, "Start call to API with URL: " + url + chosenIP + fields)
                askAPIData.execute(url + chosenIP + fields)
            } catch (e: Exception) {
                e.printStackTrace()
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
    }


    // ******************************************
    /* Manage Adapter for the list of API info */
    // ******************************************
    inner class LocationInfosAdapter : BaseAdapter {

        private var ipLocationInfosList = ArrayList<IPLocationData>()
        private var context: Context? = null

        constructor(context: Context, notesList: ArrayList<IPLocationData>) : super() {
            this.ipLocationInfosList = notesList
            this.context = context
        }

        // Necessary to define methods
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                // Link the accorgnind view to the holder
                view = layoutInflater.inflate(R.layout.locationinfos, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
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

    public fun updateListview(){

    }

    inner class ContactAPI : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg p0: String?): String?{

            var result = ""
            var url: URL
            val httpURLConnection: HttpURLConnection

            try {
                // Get info passed as parameter (AP UI URL and IP)
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
                   val jSONObject = JSONObject(result)
                   println(jSONObject)
                   val status = jSONObject.getString("status")
                   val query = jSONObject.getString("query")
                   if (status == "success"){

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
                       val message = jSONObject.getString("message")
                       textViewResults.text =  "Error (" + message + ") " + "for : " + query
                       Log.d(TAG, "FAILED : " + message + " Query : " + query)
                       }

                   // Update listvie info by the adapter in all case
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
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        // Le Bundle est une class (court terme) qui a des méthodes pour sauver
       Log.i(TAG, "onSavedInstanceState called")
        super.onSaveInstanceState(savedInstanceState)
    }

    // Permet de récupérer après
    // Sera appelé entre onStart et onResume
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        //Essaye de récupérer un compteur et voir s'il est conservé après le on stop..
        Log.i(TAG, "onRestoreInstanceState")
         super.onRestoreInstanceState(savedInstanceState)
        //val savedString = savedInstanceState.getString(TEXT_CONTENTS, "")

    }
}