package com.llamalabb.com.raipriceviewer.service.background

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.widget.RemoteViews
import com.llamalabb.com.raipriceviewer.MyApp
import com.llamalabb.com.raipriceviewer.R
import com.llamalabb.com.raipriceviewer.Settings
import com.llamalabb.com.raipriceviewer.coindetails.CoinDetailsActivity
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin_Table
import com.llamalabb.com.raipriceviewer.service.api.cmc.CMCApiService
import com.llamalabb.com.raipriceviewer.service.api.cmc.CMCClient
import com.llamalabb.com.raipriceviewer.widget.PriceViewWidget
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call



/**
 * Created by andy on 1/3/18.
 */
class CMCApiJobIntentService : JobIntentService() {
    companion object {
        const val BROADCAST_PRICE_CHANGE = "com.llamalabb.raipriceviewer.coinupdateservice"
        const val JOB_ID = 5865
        fun enqueueWork(context: Context, intent: Intent){
            enqueueWork(context, CMCApiJobIntentService::class.java, JOB_ID, intent)
        }
    }

    @SuppressLint("ApplySharedPref")
    override fun onHandleWork(intent: Intent) {
        if(isNetworkAvailable(this)) {
            MyApp.settings = this.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
            val id = intent.getStringExtra("id")
            val api: CMCApiService = CMCClient.getCoinMarketCapCoinApiService()
            val call: Call<List<CoinMarketCapCoin>> = api.getCoinMarketCapCoinInfo(id, currency)
            try{
                val coinData = call.execute().body()?.get(0)
                coinData?.save()
                MyApp.settings
                        .edit()
                        .putLong(Settings.LAST_UPDATE, System.currentTimeMillis())
                        .commit()
            } catch(e: Exception) { return }
            finally {
                broadcastWidgetUpdate()
                updateNotification()
            }
            val broadcastIntent = Intent(BROADCAST_PRICE_CHANGE)
            broadcastIntent.putExtra("resultCode", Activity.RESULT_OK)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
        }
    }

    private fun broadcastWidgetUpdate(){
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, PriceViewWidget::class.java))
        val myWidget = PriceViewWidget()
        myWidget.onUpdate(this, AppWidgetManager.getInstance(this), ids)
    }

    private fun updateNotification(){
        val isNotificationEnabled = MyApp.settings.getBoolean(Settings.IS_NOTIFICATION_ENABLED, false)
        if(isNotificationEnabled){
            val coin: CoinMarketCapCoin? = SQLite.select()
                    .from(CoinMarketCapCoin::class)
                    .where(CoinMarketCapCoin_Table.id.eq("raiblocks"))
                    .querySingle()
            val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
            var isPositive = false
            coin?.let{ isPositive = it.percent_change_24h.toDouble() >= 0 }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val remoteViews = RemoteViews(this.packageName, R.layout.notification_bar)
            remoteViews.setTextViewText(R.id.notification_fiat_price_tv, coin?.getFormattedFiatPrice())
            remoteViews.setTextViewText(R.id.notification_currency_tv, currency)
            remoteViews.setTextViewText(R.id.notification_fiat_percent_change_tv, coin?.getFormattedPercentChanged())
            remoteViews.setOnClickPendingIntent(R.id.notification_content_container, getActivityPendingIntent())
            if(isPositive) remoteViews.setTextColor(R.id.notification_fiat_percent_change_tv, ContextCompat.getColor(this, R.color.value_up))
            else remoteViews.setTextColor(R.id.notification_fiat_percent_change_tv, ContextCompat.getColor(this, R.color.value_down))

            val builder  = NotificationCompat.Builder(this, Settings.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setOngoing(true)
                    .setCustomContentView(remoteViews)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())

            notificationManager.notify(Settings.NOTIFICATION_ID, builder.build())
        }
    }

    private fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun getActivityPendingIntent() : PendingIntent{
        val intent = Intent(this, CoinDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = System.currentTimeMillis().toString()
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}