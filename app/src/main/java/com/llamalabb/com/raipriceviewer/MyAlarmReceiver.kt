package com.llamalabb.com.raipriceviewer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Created by andy on 1/3/18.
 */
class MyAlarmReceiver : BroadcastReceiver(){
    companion object {
        val REQUEST_CODE = 5000
        val ACTION = "com.llamalabb.raipriceviewer.service.alarm"
    }
    override fun onReceive(context: Context, intent: Intent) {
        if(isNetworkAvailable(context)) {
            val id = intent.getStringExtra("id")
            val i = Intent(context, ApiListenerService::class.java)
            i.putExtra("id", id)
            context.startService(i)
        }
    }
    private fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}