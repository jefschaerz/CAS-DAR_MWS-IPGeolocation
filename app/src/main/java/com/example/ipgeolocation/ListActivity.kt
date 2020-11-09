package com.example.ipgeolocation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_list.*



class ListActivity : AppCompatActivity() {
    private val TAG = ListActivity::class.java.simpleName
    val ipList = arrayOf<String>("82.78.49.255", "92.26.236.220", "81.246.23.87")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Define action Bar to return manually to Main activity
        val actionBar = supportActionBar
        actionBar!!.title = "List of IP address"
        actionBar.setDisplayHomeAsUpEnabled(true)

        // Receive and display current IP from MainActivity
        val currentIP = intent.getStringExtra("MainIpAddress")
        editTextIPAddressFromList.setText(currentIP)

        // Define adapter for the list of ip address
        val arrayAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ipList)
        listView.adapter = arrayAdapter
        // Define onclick Action
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val resultIntent = Intent()
            resultIntent.putExtra("result", adapterView.getItemAtPosition(position) as String)
            setResult(RESULT_OK, resultIntent)
            finish()
            }

        // Define action on Use this and send back to main activity
        buttonUseFromList.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result", editTextIPAddressFromList.text.toString())
            setResult(RESULT_OK, resultIntent)
            Log.i(TAG,"Result of activity : " + RESULT_OK)
            Log.i(TAG,"Value returned: " + editTextIPAddressFromList.text.toString())
            finish()
        }
    }
}