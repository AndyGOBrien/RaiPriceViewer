package com.llamalabb.com.raipriceviewer

/**
 * Created by andy on 1/22/18.
 */
object Settings {
    const val CURRENCY = "currency"
    const val LAST_UPDATE = "lastUpdate"
    const val IS_NOTIFICATION_ENABLED = "is_notification_enabled"
    const val NOTIFICATION_CHANNEL_ID = "rai_price_notification_01"
    const val NOTIFICATION_ID = 209212
    const val COIN_ID = "nano"
    val currencyMap = mapOf(
            Pair("AUD","$"),
            Pair("BRL","R$"),
            Pair("CAD","$"),
            Pair("CHF","CHF"),
            Pair("CLP","$"),
            Pair("CNY","¥"),
            Pair("CZK","Kč"),
            Pair("DKK","kr"),
            Pair("EUR","€"),
            Pair("GBP","£"),
            Pair("HKD","$"),
            Pair("HUF","Ft"),
            Pair("IDR","Rp"),
            Pair("ILS","₪"),
            Pair("INR","₹"),
            Pair("JPY","¥"),
            Pair("KRW","₩"),
            Pair("MXN","$"),
            Pair("MYR","RM"),
            Pair("NOK","kr"),
            Pair("NZD","$"),
            Pair("PHP","₱"),
            Pair("PKR","₨"),
            Pair("PLN","zł"),
            Pair("RUB","₽"),
            Pair("SEK","kr"),
            Pair("SGD","$"),
            Pair("THB","฿"),
            Pair("TRY","₺"),
            Pair("TWD","NT$"),
            Pair("USD","$"),
            Pair("ZAR","R")
    )
}