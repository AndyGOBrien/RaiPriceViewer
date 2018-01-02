package com.llamalabb.com.raipriceviewer.retrofit

import com.llamalabb.com.raipriceviewer.CoinMarketCapApiObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by andyg on 1/2/2018.
 */
interface ApiService {
    @GET("/v1/ticker")
    fun getCoinMarketCapListInfo(
            @Query(value="limit") limit: String = "100",
            @Query(value="start") start: String = "0",
            @Query(value="convert") convert: String = "USD")
            : Call<CoinMarketCapApiObject>

    @GET("/v1/ticket/{id}")
    fun getCoinMarketCapObjectInfo(@Path(value="id") ticker: String?): Call<CoinMarketCapApiObject>

}