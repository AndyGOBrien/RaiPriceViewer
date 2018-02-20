package com.llamalabb.com.raipriceviewer.coindetails

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.DialogFragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.RemoteViews
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.gson.JsonObject
import com.llamalabb.com.raipriceviewer.*
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin_Table
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin_Table.id
import com.llamalabb.com.raipriceviewer.service.api.cmc.CMCApiService
import com.llamalabb.com.raipriceviewer.service.api.cmc.CMCClient
import com.llamalabb.com.raipriceviewer.service.api.nanode.NanodeBody
import com.llamalabb.com.raipriceviewer.service.api.nanode.NanodeClient
import com.llamalabb.com.raipriceviewer.service.background.CMCApiJobIntentService
import com.llamalabb.com.raipriceviewer.service.background.MyAlarmReceiver
import com.llamalabb.com.raipriceviewer.widget.PriceViewWidget
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_coin_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoinDetailsActivity :
        AppCompatActivity(),
        CoinDetailsContract.CoinDetailsView,
        SelectGridItemDialogFragment.ItemSelectCallBack {

    override var presenter: CoinDetailsContract.CoinDetailsPresenter = CoinDetailsPresenter(this)
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val UPDATE_DELAY: Long = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_details)
        setupServiceReceiver()
        setNotificationBar()
        currency_select_button.setOnClickListener { showCurrencySelectDialog() }
        currency_select_button.text = MyApp.settings.getString(Settings.CURRENCY, "USD")

        MobileAds.initialize(this, getString(R.string.mobile_ad))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

    }

    override fun itemClicked(text: String) {
        MyApp.settings.edit().putString(Settings.CURRENCY, text).commit()
        currency_select_button.text = text
        val prev = supportFragmentManager.findFragmentByTag("select_item")
        prev?.let { (prev as DialogFragment).dismiss() }
        runService(true)
    }

    fun runService(isForced: Boolean) {
        val currentTime = System.currentTimeMillis()
        val coinLastUpdated: Long = currentTime - MyApp.settings.getLong(Settings.LAST_UPDATE, currentTime - UPDATE_DELAY)

        if (coinLastUpdated >= UPDATE_DELAY || isForced) {
            Log.d("CoinDetailsActivity", "Service run")
            val intent = Intent(this, CMCApiJobIntentService::class.java)
            intent.putExtra("id", Settings.COIN_ID)
            coinUpdateFromApi()
        }
    }


    private fun broadcastWidgetUpdate(){
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, PriceViewWidget::class.java))
        val myWidget = PriceViewWidget()
        myWidget.onUpdate(this, AppWidgetManager.getInstance(this), ids)
    }

    private fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    fun coinUpdateFromApi() {
        if (isNetworkAvailable(this)) {
            MyApp.settings = this.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
            val api: CMCApiService = CMCClient.getCoinMarketCapCoinApiService()
            val call: Call<List<CoinMarketCapCoin>> = api.getCoinMarketCapCoinInfo(Settings.COIN_ID, currency)
                call.enqueue(object: Callback<List<CoinMarketCapCoin>>{
                override fun onFailure(call: Call<List<CoinMarketCapCoin>>?, t: Throwable?) {}
                override fun onResponse(call: Call<List<CoinMarketCapCoin>>?, response: Response<List<CoinMarketCapCoin>>?) {
                    val coinData = response?.body()?.get(0)
                    coinData?.let{ it.save() }
                    MyApp.settings
                            .edit()
                            .putLong(Settings.LAST_UPDATE, System.currentTimeMillis())
                            .commit()
                    coinUpdate()
                }
            })

        }
    }


    private fun setupServiceReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    CMCApiJobIntentService.BROADCAST_PRICE_CHANGE -> coinUpdate()
                }
            }
        }
    }

    private fun coinUpdate() {
        val coin: CoinMarketCapCoin? = SQLite.select()
                .from(CoinMarketCapCoin::class)
                .where(CoinMarketCapCoin_Table.id.eq(Settings.COIN_ID))
                .querySingle()
        coin?.let {
            broadcastWidgetUpdate()
            setNotificationBar()
            showCoinInfo(it)
        }
    }

    fun showCoinInfo(coin: CoinMarketCapCoin) {
        val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
        val percentChange = coin.percent_change_24h.toDouble()

        setPercentChangeColor(percentChange >= 0, fiat_percent_change_tv)
        fiat_price_tv.text = coin.getFormattedFiatPrice()
        currency_tv.text = currency
        price_crypt_tv.text = coin.getFormattedBTCPrice()
        fiat_percent_change_tv.text = coin.getFormattedPercentChanged()
        market_cap_tv.text = coin.getFormattedMarketCap()
        volume_tv.text = coin.getFormattedVolume()
        rank_tv.text = "Rank: ${coin.rank}"
    }

    fun showCurrencySelectDialog() {
        val fm = supportFragmentManager
        val frag = SelectGridItemDialogFragment.newInstance("Select Currency",
                resources.getStringArray(R.array.currency_array))
        frag.show(fm, "select_item")
    }

    fun setPercentChangeColor(isPositive: Boolean, textView: TextView) {
        if (isPositive)
            textView.setTextColor(ContextCompat.getColor(this, R.color.value_up))
        else
            textView.setTextColor(ContextCompat.getColor(this, R.color.value_down))
    }

    private fun scheduleAlarm() {
        val i = Intent(this, MyAlarmReceiver::class.java)
        i.putExtra("id", Settings.COIN_ID)
        i.action = MyAlarmReceiver.ACTION
        val pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 30000, 300000, pIntent)
    }

    fun setNotificationBar() {
        val isNotificationEnabled = MyApp.settings.getBoolean(Settings.IS_NOTIFICATION_ENABLED, false)
        toggle_notification_bar.isChecked = isNotificationEnabled
        toggle_notification_bar.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked) setNotificationBarOn() else setNotificationBarOff()
        }
    }

    fun setNotificationBarOn(){
        Log.d("CoinDetailsActivity", "Notification On")
        val coin: CoinMarketCapCoin? = SQLite.select()
                .from(CoinMarketCapCoin::class)
                .where(CoinMarketCapCoin_Table.id.eq(Settings.COIN_ID))
                .querySingle()
        val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
        var isPositive = false
        coin?.let{ isPositive = it.percent_change_24h.toDouble() >= 0 }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                    NotificationChannel(Settings.NOTIFICATION_CHANNEL_ID,
                            "Nano Notification Channel",
                            NotificationManager.IMPORTANCE_LOW))
        }
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
        MyApp.settings.edit().putBoolean(Settings.IS_NOTIFICATION_ENABLED, true).apply()
    }

    fun setNotificationBarOff(){
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Settings.NOTIFICATION_ID)
        MyApp.settings.edit().putBoolean(Settings.IS_NOTIFICATION_ENABLED, false).apply()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                IntentFilter(CMCApiJobIntentService.BROADCAST_PRICE_CHANGE))
        scheduleAlarm()
        coinUpdate()
        runService(false)
    }

    private fun getActivityPendingIntent() : PendingIntent{
        val intent = Intent(this, CoinDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = System.currentTimeMillis().toString()
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}