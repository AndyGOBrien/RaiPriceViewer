package com.llamalabb.com.raipriceviewer.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.widget.RemoteViews
import android.widget.TextView
import com.llamalabb.com.raipriceviewer.*
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin_Table
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite

/**
 * Created by andy on 1/25/18.
 */
class PriceViewWidget : AppWidgetProvider() {

    companion object {
        const val ACTION = "android.raiwidget.action.RAIWIDGET_UPDATE"
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val coin: CoinMarketCapCoin? = SQLite.select()
                .from(CoinMarketCapCoin::class)
                .where(CoinMarketCapCoin_Table.id.eq("raiblocks"))
                .querySingle()

        appWidgetIds?.forEach {id ->
            coin?.let {
                updateWidget(context, appWidgetManager, id, it)
            }
        }
    }

    private fun updateWidget(context: Context?, appWidgetManager: AppWidgetManager?, appwidgetId: Int, coin: CoinMarketCapCoin){
        val views = RemoteViews(context?.packageName, R.layout.widget_layout)

        val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
        val currencySymbol = Settings.currencyMap[currency]
        val fiatPrice: String?
        val marketCap: String?
        val volume: String?
        val percentChange = coin.percent_change_24h.toDouble()
        val absPercentChange = percentChange.toTwoDecimalPlacesAbs()
        val btcPrice = coin.price_btc

        if (currency == "USD") {
            fiatPrice = coin.price_usd.toDouble().toTwoDecimalPlaces()
            marketCap = coin.market_cap_usd.formatNumber()
            volume = coin.volume_usd.formatNumber()
        } else {
            fiatPrice = coin.altCurrencyPrice?.toDouble()?.toTwoDecimalPlaces()
            marketCap = coin.altCurrencyMarketCap?.formatNumber()
            volume = coin.altCurrencyVol?.formatNumber()
        }

        views.setTextViewText(R.id.widget_fiat_price_tv, currencySymbol + fiatPrice)
        views.setTextViewText(R.id.widget_currency_tv, currency)
        views.setTextViewText(R.id.widget_fiat_percent_change_tv,"($absPercentChange%)")
        views.setTextViewText(R.id.widget_price_crypt_tv, "$btcPrice BTC")
        setPercentChangeColor(context, percentChange >= 0, views)

        appWidgetManager?.updateAppWidget(appwidgetId, views)
    }

    fun setPercentChangeColor(context: Context?,isPositive: Boolean, views: RemoteViews) {

        if (isPositive)
            views.setTextColor(R.id.widget_fiat_percent_change_tv, ContextCompat.getColor(context, R.color.value_up))
        else
            views.setTextColor(R.id.widget_fiat_percent_change_tv, ContextCompat.getColor(context, R.color.value_down))
    }
}