package com.llamalabb.com.raipriceviewer.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.llamalabb.com.raipriceviewer.formatNumber
import com.llamalabb.com.raipriceviewer.toTwoDecimalPlaces
import com.llamalabb.com.raipriceviewer.toTwoDecimalPlacesAbs
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

/**
 * Created by andyg on 1/2/2018.
 */
@Table(database = AppDatabase::class)
data class CoinMarketCapCoin(
        @PrimaryKey
        @SerializedName("id")
        @Expose
        var id: String = "",
        @Column
        @SerializedName("name")
        @Expose
        var name: String = "",
        @Column
        @SerializedName("symbol")
        @Expose
        var symbol: String = "",
        @Column
        @SerializedName("rank")
        @Expose
        var rank: String = "",
        @Column
        @SerializedName("price_usd")
        @Expose
        var price_usd: String = "",
        @Column
        @SerializedName("price_btc")
        @Expose
        var price_btc: String = "",
        @Column
        @SerializedName("24h_volume_usd")
        @Expose
        var volume_usd: String = "",
        @Column
        @SerializedName("market_cap_usd")
        @Expose
        var market_cap_usd: String = "",
        @Column
        @SerializedName("available_supply")
        @Expose
        var available_supply: String = "",
        @Column
        @SerializedName("total_supply")
        @Expose
        var total_supply: String = "" ,
        @Column
        @SerializedName("max_supply")
        @Expose
        var max_supply: String = "",
        @Column
        @SerializedName("percent_change_1h")
        @Expose
        var percent_change_1h: String = "",
        @Column
        @SerializedName("percent_change_24h")
        @Expose
        var percent_change_24h: String = "",
        @Column
        @SerializedName("percent_change_7d")
        @Expose
        var percent_change_7d: String = "",
        @Column
        @SerializedName("last_updated")
        @Expose
        var last_updated: String = "",
        @Column
        @SerializedName(value="price_eur", alternate =
                        ["price_aud",
                        "price_brl",
                        "price_cad",
                        "price_chf",
                        "price_clp",
                        "price_gbp",
                        "price_hkd",
                        "price_huf",
                        "price_idr",
                        "price_ils",
                        "price_inr",
                        "price_jpy",
                        "price_krw",
                        "price_mxn",
                        "price_myr",
                        "price_nok",
                        "price_nzd",
                        "price_php",
                        "price_pkr",
                        "price_pln",
                        "price_rub",
                        "price_sek",
                        "price_sgd",
                        "price_thb",
                        "price_try",
                        "price_twd",
                        "price_zar"])
        @Expose
        var altCurrencyPrice: String? = null,
        @Column
        @SerializedName(value="24h_volume_eur",alternate =
                        ["24h_volume_aud",
                        "24h_volume_brl",
                        "24h_volume_cad",
                        "24h_volume_chf",
                        "24h_volume_clp",
                        "24h_volume_gbp",
                        "24h_volume_hkd",
                        "24h_volume_huf",
                        "24h_volume_idr",
                        "24h_volume_ils",
                        "24h_volume_inr",
                        "24h_volume_jpy",
                        "24h_volume_krw",
                        "24h_volume_mxn",
                        "24h_volume_myr",
                        "24h_volume_nok",
                        "24h_volume_nzd",
                        "24h_volume_php",
                        "24h_volume_pkr",
                        "24h_volume_pln",
                        "24h_volume_rub",
                        "24h_volume_sek",
                        "24h_volume_sgd",
                        "24h_volume_thb",
                        "24h_volume_try",
                        "24h_volume_twd",
                        "24h_volume_zar"])
        @Expose
        var altCurrencyVol: String? = null,
        @Column
        @SerializedName(value="market_cap_eur", alternate =
                        ["market_cap_aud",
                        "market_cap_brl",
                        "market_cap_cad",
                        "market_cap_chf",
                        "market_cap_clp",
                        "market_cap_gbp",
                        "market_cap_hkd",
                        "market_cap_huf",
                        "market_cap_idr",
                        "market_cap_ils",
                        "market_cap_inr",
                        "market_cap_jpy",
                        "market_cap_krw",
                        "market_cap_mxn",
                        "market_cap_myr",
                        "market_cap_nok",
                        "market_cap_nzd",
                        "market_cap_php",
                        "market_cap_pkr",
                        "market_cap_pln",
                        "market_cap_rub",
                        "market_cap_sek",
                        "market_cap_sgd",
                        "market_cap_thb",
                        "market_cap_try",
                        "market_cap_twd",
                        "market_cap_zar"])
        @Expose
        var altCurrencyMarketCap: String? = null
)