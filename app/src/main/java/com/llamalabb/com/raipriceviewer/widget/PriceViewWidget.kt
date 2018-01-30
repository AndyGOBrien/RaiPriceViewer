package com.llamalabb.com.raipriceviewer.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.RemoteViews
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
        val percentChange = coin.percent_change_24h.toDouble()

        views.setTextViewText(R.id.widget_fiat_price_tv, coin.getFormattedFiatPrice())
        views.setTextViewText(R.id.widget_currency_tv, currency)
        views.setTextViewText(R.id.widget_fiat_percent_change_tv,coin.getFormattedPercentChanged())
        views.setTextViewText(R.id.widget_price_crypt_tv, coin.getFormattedBTCPrice())
        setPercentChangeColor(context, percentChange >= 0, views)

        appWidgetManager?.updateAppWidget(appwidgetId, views)
    }

    private fun setPercentChangeColor(context: Context?,isPositive: Boolean, views: RemoteViews) {
        if (isPositive)
            views.setTextColor(R.id.widget_fiat_percent_change_tv, ContextCompat.getColor(context, R.color.value_up))
        else
            views.setTextColor(R.id.widget_fiat_percent_change_tv, ContextCompat.getColor(context, R.color.value_down))
    }
}