package com.example.ipgeolocation

import android.R.array
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_main.*


class ListActivity : AppCompatActivity() {
    private val TAG = ListActivity::class.java.simpleName
    var ipListComments = ArrayList<IpListComment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        Log.i(TAG, "OnCreate in ListActivity")

        // Define action Bar to return manually to Main activity
        val actionBar = supportActionBar
        actionBar!!.title = "List of IP address"
        actionBar.setDisplayHomeAsUpEnabled(true)

        // Receive and display current IP from MainActivity
        val currentIP = intent.getStringExtra("MainIpAddress")
        editTextNewIP.setText(currentIP)

        var ipListAdapter = IPsAdapter(this, ipListComments)
        listViewComments.adapter = ipListAdapter

        loadIPListData()

        // Define On Long Click --> Delete It
        listViewComments.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, position, id ->
            // Display confirmation alert
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete items")
            builder.setMessage("Are your sure to delete")
            builder.setPositiveButton("Yes", { dialogInterface: DialogInterface, i: Int ->
                ipListComments.remove(ipListComments[position])
                (listViewComments.adapter as BaseAdapter).notifyDataSetChanged()
            })
            builder.setNegativeButton("No", { dialogInterface: DialogInterface, i: Int -> null })
            builder.show()
            true
        }

        // Define On Click event --> Use it
        listViewComments.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val resultIntent = Intent()
            resultIntent.putExtra("result", ipListComments[position].ipAddress as String)
            setResult(RESULT_OK, resultIntent)
            // TODO : Find all places to save
            saveIPListData()
            finish()
        }
        // Define action on clera IP button
        buttonClearIP.setOnClickListener {
            editTextNewIP.setText("")
        }

        // Define action on Use this and send back to main activity
        buttonAddNewIP.setOnClickListener {
            // TODO : Create global func for this
            val isValidIP = IPAddressValidation.isValidIPAddress(editTextNewIP.text.toString()) ;
            Log.d(TAG, "IP :" + editTextNewIP.text.toString())
            if (!isValidIP)
            // IP NOT Valid --> Inform user by Toast
                Toast.makeText(
                    this,
                    String.format(getString(R.string.messageprovidedIPnotvalid)),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                ipListComments.add(
                    IpListComment(
                        editTextNewIP.text.toString(),
                        editTextNewComment.text.toString()
                    )
                )
                (listViewComments.adapter as BaseAdapter).notifyDataSetChanged()
                // Clear fields values
                editTextNewIP.setText("")
                editTextNewComment.setText("")
            }
        }

        // Define action on Add IP values
        buttonAddFakes.setOnClickListener {
            // Create list of IP
            ipListComments.add(IpListComment("178.197.249.50", "Lucerne CH"))
            ipListComments.add(IpListComment("92.26.236.220", "Sheffield UK"))
            ipListComments.add(IpListComment("1.1.1.1", "South Brisbane AUS"))
            ipListComments.add(IpListComment("81.246.23.87", "Liège BE"))
            ipListComments.add(IpListComment("192.168.27.254", "Error - Local range"))
            ipListComments.add(IpListComment("260.26.236.220", "Error - Bad IP format"))
            (listViewComments.adapter as BaseAdapter).notifyDataSetChanged()
        }
        buttonClear.setOnClickListener {
            ipListComments.clear()
            clearIPListData()
            (listViewComments.adapter as BaseAdapter).notifyDataSetChanged()
        }
    }

    inner class IPsAdapter : BaseAdapter {

        private var ipList = ArrayList<IpListComment>()
        private var context: Context? = null

        constructor(context: Context, ipList: ArrayList<IpListComment>) : super() {
            this.ipList = ipList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.ipinfos, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvIP.text = ipList[position].ipAddress
            vh.tvComment.text = ipList[position].comments

            return view
        }

        override fun getItem(position: Int): Any {
            return ipList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return ipList.size
        }
    }

    private class ViewHolder(view: View?) {
        val tvIP: TextView
        val tvComment: TextView

        init {
            this.tvIP = view?.findViewById(R.id.tvIP) as TextView
            this.tvComment = view?.findViewById(R.id.tvComment) as TextView
        }
    }

    // ******************************************
    /* Load and Save IP List in SharedPreferences */
    // ******************************************
    private fun loadIPListData(){
        // Get handle to shared preferences
        val sharedPref = this?.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
        val defaultValue = "No set"
        // Retrieve last IPAddress
        val ItemsNb = sharedPref.getInt(IP_ITEM_NB, 0)
        Log.i(TAG, "In LoadData - Items: " + ItemsNb.toString())
        for (i in 0 until ItemsNb)  {
            Log.i(TAG, "IP :" + sharedPref.getString(IP_ITEM_ + i.toString() + "_IP" , defaultValue))
            Log.i(TAG, "Comment :" + sharedPref.getString(IP_ITEM_ + i.toString() + "_Comment" , defaultValue))
            val receivedIp = sharedPref.getString(IP_ITEM_ + i.toString() + "_IP" , defaultValue)
            val receivedComment = sharedPref.getString(IP_ITEM_ + i.toString() + "_Comment" , defaultValue)
            ipListComments.add(IpListComment(receivedIp!!, receivedComment!!))
        }
    }

    private fun saveIPListData(){
        // Get handle to shared preferences
        val sharedPref = this?.getSharedPreferences(
            SHAREDPREF, Context.MODE_PRIVATE
        )
        Log.i(TAG, "SaveData in ListActivity - Items :" + ipListComments.count().toString())
        with(sharedPref.edit()) {
            // Clear all previous preferences !!
            // clear()

            // Save all IP and comments
            putInt(IP_ITEM_NB , ipListComments.count())
            for (i in 0 until ipListComments.count())  {
                putString(IP_ITEM_ + i + "_IP", ipListComments.get(i).ipAddress)
                putString(IP_ITEM_ + i + "_Comment", ipListComments.get(i).comments)
            }
            apply()
        }
    }

    private fun clearIPListData(){
        // Get handle to shared preferences
        val sharedPref = this?.getSharedPreferences(
            SHAREDPREF, Context.MODE_PRIVATE
        )
        Log.i(TAG, "Clear in shared Preferences - All Items :" + ipListComments.count().toString())
        with(sharedPref.edit()) {
            // Clear all previous preferences !!
            clear()
            apply()
        }
    }

}