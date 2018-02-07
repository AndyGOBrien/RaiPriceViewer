package com.llamalabb.com.raipriceviewer.service.api.nanode

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by andy on 2/7/18.
 */
object NanodeClient {

    private const val BASE_URL = "https://api.nanode.co"

    private fun getAccountInstance() : Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun getNanodeApi() : NanodeApiService = getAccountInstance().create(NanodeApiService::class.java)

}