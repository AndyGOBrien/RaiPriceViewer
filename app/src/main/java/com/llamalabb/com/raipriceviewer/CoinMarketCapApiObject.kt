package com.llamalabb.com.raipriceviewer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by andyg on 1/2/2018.
 */
data class CoinMarketCapApiObject(
        @SerializedName("id")
        @Expose
        val id: String,
        @SerializedName("name")
        @Expose
        val name: String,
        @SerializedName("symbol")
        @Expose
        val symbol: String,
        @SerializedName("rank")
        @Expose
        var rank: String,
        @SerializedName("price_usd")
        @Expose
        var price_usd: String,
        @SerializedName("price_btc")
        @Expose
        var price_btc: String,
        @SerializedName("24h_volume_usd")
        @Expose
        var volume_usd: String,
        @SerializedName("market_cap_usd")
        @Expose
        var market_cap_usd: String,
        @SerializedName("available_supply")
        @Expose
        var available_supply: String,
        @SerializedName("total_supply")
        @Expose
        var total_supply: String,
        @SerializedName("max_supply")
        @Expose
        var max_supply: String,
        @SerializedName("percent_change_1h")
        @Expose
        var percent_change_1h: String,
        @SerializedName("percent_change_24h")
        @Expose
        var percent_change_24h: String,
        @SerializedName("percent_change_7d")
        @Expose
        var percent_change_7d: String,
        @SerializedName("last_updated")
        @Expose
        var last_updated: String
)