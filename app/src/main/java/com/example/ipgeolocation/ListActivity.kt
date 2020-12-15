package com.example.ipgeolocation

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_main.*

class ListActivity : AppCompatActivity() {
    private val TAG = ListActivity::class.java.simpleName
    // TODO : Create list with IP and infos (See TestListElargi)
    // val ipList = arrayOf<String>("82.78.49.255", "192.168.27.254", "92.26.236.220", "81.246.23.87", "280.12.13.14", "178.197.249.50")
    var ipListComments = ArrayList<IpListComment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Define action Bar to return manually to Main activity
        val actionBar = supportActionBar
        actionBar!!.title = "List of IP address"
        actionBar.setDisplayHomeAsUpEnabled(true)

        // Receive and display current IP from MainActivity
        val currentIP = intent.getStringExtra("MainIpAddress")
        editTextNewIP.setText(currentIP)

        var ipListAdapter = IPsAdapter(this, ipListComments)
        listViewComments.adapter = ipListAdapter

        // Define On Long Click --> Delete It
        listViewComments.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, position, id ->
            // Display confirmation alert
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete items")
            builder.setMessage("Are your sure to delete")
            builder.setPositiveButton("Yes", {dialogInterface : DialogInterface, i: Int ->
                ipListComments.remove(ipListComments[position])
                (listViewComments.adapter as BaseAdapter).notifyDataSetChanged()
            })
            builder.setNegativeButton("No", {dialogInterface : DialogInterface, i: Int -> null})
            builder.show()
            true
        }

        // Define On Click event --> Use it
        listViewComments.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val resultIntent = Intent()
            resultIntent.putExtra("result", ipListComments[position].ipAddress as String)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Define action on Use this and send back to main activity
        buttonAddNewIP.setOnClickListener {
            ipListComments.add(IpListComment(editTextNewIP.text.toString(), editTextNewComment.text.toString()))
            (listViewComments.adapter as BaseAdapter).notifyDataSetChanged()
            // Clear fields values
            editTextNewIP.setText("")
            editTextNewComment.setText("")
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

            vh.tvTitle.text = ipList[position].ipAddress
            vh.tvContent.text = ipList[position].comments

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
        val tvTitle: TextView
        val tvContent: TextView

        init {
            this.tvTitle = view?.findViewById(R.id.tvTitle) as TextView
            this.tvContent = view?.findViewById(R.id.tvContent) as TextView
        }
    }

}