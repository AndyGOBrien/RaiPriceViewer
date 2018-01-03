package com.llamalabb.com.raipriceviewer

import android.app.IntentService
import android.content.Intent
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.retrofit.ApiService
import com.llamalabb.com.raipriceviewer.retrofit.RetroClient
import retrofit2.Call


/**
 * Created by andy on 1/3/18.
 */
object ApiListenerService : IntentService("CoinMarketCapApiListener") {
    override fun onHandleIntent(intent: Intent) {
        val id = intent.getStringExtra("id")
        val api: ApiService = RetroClient.getCoinMarketCapCoinApiService()
        val call: Call<List<CoinMarketCapCoin>> = api.getCoinMarketCapCoinInfo(id)
        val coinData = call.execute().body()?.get(0)
        coinData?.save()
    }
}