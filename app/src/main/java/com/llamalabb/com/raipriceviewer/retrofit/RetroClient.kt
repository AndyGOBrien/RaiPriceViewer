package com.llamalabb.com.raipriceviewer.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by andyg on 1/2/2018.
 */
object RetroClient {

    private val COIN_MARKET_CAP_BASE_URL = "https://api.coinmarketcap.com"

    private fun getCoinMarketCapCoinRetrofitInstance() : Retrofit{
        return Retrofit.Builder()
                .baseUrl(COIN_MARKET_CAP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun getCoinMarketCapCoinApiService() : ApiService = getCoinMarketCapCoinRetrofitInstance().create(ApiService::class.java)
}