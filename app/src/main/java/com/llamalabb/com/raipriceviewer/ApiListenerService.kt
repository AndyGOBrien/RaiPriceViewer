package com.llamalabb.com.raipriceviewer

import android.app.Activity
import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.llamalabb.com.raipriceviewer.model.AppDatabase
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.retrofit.ApiService
import com.llamalabb.com.raipriceviewer.retrofit.RetroClient
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.save
import retrofit2.Call


/**
 * Created by andy on 1/3/18.
 */
class ApiListenerService : IntentService("CoinMarketCapApiListener") {
    companion object {
        val BROADCAST_PRICE_CHANGE = "com.llamalabb.raipriceviewer.coinupdateservice"
    }
    override fun onHandleIntent(intent: Intent) {
        initDatabase()
        val id = intent.getStringExtra("id")
        val api: ApiService = RetroClient.getCoinMarketCapCoinApiService()
        val call: Call<List<CoinMarketCapCoin>> = api.getCoinMarketCapCoinInfo(id)
        val coinData = call.execute().body()?.get(0)
        coinData?.save()

        val broadcastIntent = Intent(BROADCAST_PRICE_CHANGE)
        broadcastIntent.putExtra("resultCode", Activity.RESULT_OK)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun initDatabase(){
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(AppDatabase::class.java)
                        .databaseName("AppDatabase")
                        .build())
                .build())
    }
}