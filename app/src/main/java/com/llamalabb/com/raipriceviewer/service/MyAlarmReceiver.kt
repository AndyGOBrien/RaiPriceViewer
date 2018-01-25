package com.llamalabb.com.raipriceviewer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

/**
 * Created by andy on 1/3/18.
 */
class MyAlarmReceiver : BroadcastReceiver(){
    companion object {
        const val REQUEST_CODE = 5000
        const val ACTION = "com.llamalabb.raipriceviewer.service.alarm"
    }
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MyAlarmReceiver", "Alarm Started")
        if(isNetworkAvailable(context)) {
            val id = intent.getStringExtra("id")
            val i = Intent(context, CMCApiJobIntentService::class.java)
            i.putExtra("id", id)
            CMCApiJobIntentService.enqueueWork(context, i)
        }
        Log.d("MyAlarmReceiver", "Alarm Ended")
    }
    private fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}