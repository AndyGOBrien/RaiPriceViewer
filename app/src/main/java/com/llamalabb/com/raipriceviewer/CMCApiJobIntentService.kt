package com.llamalabb.com.raipriceviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v4.app.JobIntentService
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.retrofit.ApiService
import com.llamalabb.com.raipriceviewer.retrofit.RetroClient
import com.raizlabs.android.dbflow.kotlinextensions.save
import retrofit2.Call


/**
 * Created by andy on 1/3/18.
 */
class CMCApiJobIntentService : JobIntentService() {
    companion object {
        const val BROADCAST_PRICE_CHANGE = "com.llamalabb.raipriceviewer.coinupdateservice"
        const val JOB_ID = 5865
        fun enqueueWork(context: Context, intent: Intent){
            enqueueWork(context, CMCApiJobIntentService::class.java, JOB_ID, intent)
        }
    }

    @SuppressLint("ApplySharedPref")
    override fun onHandleWork(intent: Intent) {
        Log.d("CMCApiJobIntentService", "Service Started")
        if(isNetworkAvailable(this)) {
            MyApp.settings = this.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
            val id = intent.getStringExtra("id")
            val api: ApiService = RetroClient.getCoinMarketCapCoinApiService()
            val call: Call<List<CoinMarketCapCoin>> = api.getCoinMarketCapCoinInfo(id, currency)
            try{
                val coinData = call.execute().body()?.get(0)
                coinData?.save()
                MyApp.settings
                        .edit()
                        .putLong(Settings.LAST_UPDATE, System.currentTimeMillis())
                        .commit()
                Log.d("CMCApiJobIntentService", "Network Call and DB storage Complete")
            } catch(e: Exception) { return }
            val broadcastIntent = Intent(BROADCAST_PRICE_CHANGE)
            broadcastIntent.putExtra("resultCode", Activity.RESULT_OK)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
        }
        Log.d("CMCApiJobIntentService", "Service Ended")
    }

    private fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}