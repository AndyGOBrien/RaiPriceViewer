package com.llamalabb.com.raipriceviewer.service.api.nanode

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by andy on 2/7/18.
 */
interface NanodeApiService {
    @POST("/")
    @Headers("Content-Type: application/json", "Authorization: 805daa01-0c27-11e8-94a0-5334e8f562d9")
    fun getRpcResponse(@Body body: HashMap<String, String>): Call<JsonObject>
}