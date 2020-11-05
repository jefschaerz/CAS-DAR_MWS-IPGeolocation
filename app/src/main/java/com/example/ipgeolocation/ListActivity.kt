package com.example.ipgeolocation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    private val TAG = ListActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Define action Bar to return manually to Main activity
        val actionBar = supportActionBar
        actionBar!!.title = "List of IP address"
        actionBar.setDisplayHomeAsUpEnabled(true)

        // Receive and use current IP from MainActivity
        val currentIP = intent.getStringExtra("MainIpAddress")

        editTextIPAddressFromList.setText(currentIP)

        // Define action on Use this and send back to main activity
        buttonUseFromList.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("result", editTextIPAddressFromList.text.toString())
            setResult(Activity.RESULT_OK, resultIntent)
            Log.i(TAG,"Result of activity : " + Activity.RESULT_OK.toString())
            Log.i(TAG,"Value returned: " + editTextIPAddressFromList.text.toString())
            finish()
        }
    }
}