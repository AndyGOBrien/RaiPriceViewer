package com.llamalabb.com.raipriceviewer.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by andyg on 1/2/2018.
 */
@Table(database = AppDatabase::class)
data class CoinMarketCapCoin(
        @PrimaryKey
        @SerializedName("id")
        @Expose
        val id: String,
        @Column
        @SerializedName("name")
        @Expose
        val name: String,
        @Column
        @SerializedName("symbol")
        @Expose
        val symbol: String,
        @Column
        @SerializedName("rank")
        @Expose
        var rank: String,
        @Column
        @SerializedName("price_usd")
        @Expose
        var price_usd: String,
        @Column
        @SerializedName("price_btc")
        @Expose
        var price_btc: String,
        @Column
        @SerializedName("24h_volume_usd")
        @Expose
        var volume_usd: String,
        @Column
        @SerializedName("market_cap_usd")
        @Expose
        var market_cap_usd: String,
        @Column
        @SerializedName("available_supply")
        @Expose
        var available_supply: String,
        @Column
        @SerializedName("total_supply")
        @Expose
        var total_supply: String,
        @Column
        @SerializedName("max_supply")
        @Expose
        var max_supply: String,
        @Column
        @SerializedName("percent_change_1h")
        @Expose
        var percent_change_1h: String,
        @Column
        @SerializedName("percent_change_24h")
        @Expose
        var percent_change_24h: String,
        @Column
        @SerializedName("percent_change_7d")
        @Expose
        var percent_change_7d: String,
        @Column
        @SerializedName("last_updated")
        @Expose
        var last_updated: String
) : BaseModel()