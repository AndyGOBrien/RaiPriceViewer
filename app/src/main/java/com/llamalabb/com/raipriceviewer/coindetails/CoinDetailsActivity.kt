package com.llamalabb.com.raipriceviewer.coindetails

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.llamalabb.com.raipriceviewer.*
import com.llamalabb.com.raipriceviewer.R
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin_Table
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_coin_details.*

class CoinDetailsActivity :
        AppCompatActivity(),
        CoinDetailsContract.CoinDetailsView,
        SelectGridItemDialogFragment.ItemSelectCallBack {

    override var presenter: CoinDetailsContract.CoinDetailsPresenter = CoinDetailsPresenter(this)
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_details)
        setupServiceReceiver()
        currency_select_button.setOnClickListener { showCurrencySelectDialog() }
        runService(false)
        currency_select_button.text = MyApp.settings.getString(Settings.CURRENCY, "USD")

        MobileAds.initialize(this, getString(R.string.mobile_ad))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun itemClicked(text: String) {
        MyApp.settings.edit().putString(Settings.CURRENCY, text).commit()
        currency_select_button.text = text
        val prev = supportFragmentManager.findFragmentByTag("select_item")
        prev?.let{ (prev as DialogFragment).dismiss() }
        runService(true)
    }

    fun runService(isForced: Boolean){
        val coin: CoinMarketCapCoin? = SQLite.select()
                .from(CoinMarketCapCoin::class)
                .where(CoinMarketCapCoin_Table.id.eq("raiblocks"))
                .querySingle()

        val coinLastUpdated = if(coin != null) {
            System.currentTimeMillis() - coin.last_updated.toLong()
        } else 0
        if(coinLastUpdated <= 10000 || isForced) {
            val intent = Intent(this, ApiListenerService::class.java)
            intent.putExtra("id", "raiblocks")
            startService(intent)
        }
    }

    private fun setupServiceReceiver(){
        broadcastReceiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent?.action){
                    ApiListenerService.BROADCAST_PRICE_CHANGE -> coinUpdate()
                }
            }
        }
    }

    private fun coinUpdate(){
        val coin: CoinMarketCapCoin? = SQLite.select()
                .from(CoinMarketCapCoin::class)
                .where(CoinMarketCapCoin_Table.id.eq("raiblocks"))
                .querySingle()
        coin?.let {showCoinInfo(it)}
    }

    fun showCoinInfo(coin: CoinMarketCapCoin){
        val currency = MyApp.settings.getString(Settings.CURRENCY, "USD")
        val currencySymbol = Settings.currencyMap[currency]
        val fiatPrice: String?
        val marketCap: String?
        val volume: String?
        val percentChange = coin.percent_change_24h.toDouble()
        val absPercentChange = percentChange.toTwoDecimalPlacesAbs()
        val btcPrice = coin.price_btc

        if(currency == "USD"){
            fiatPrice = coin.price_usd.toDouble().toTwoDecimalPlaces()
            marketCap = coin.market_cap_usd.formatNumber()
            volume = coin.volume_usd.formatNumber()
        } else {
            fiatPrice = coin.altCurrencyPrice?.toDouble()?.toTwoDecimalPlaces()
            marketCap = coin.altCurrencyMarketCap?.formatNumber()
            volume = coin.altCurrencyVol?.formatNumber()
        }

        setPercentChangeColor(percentChange >= 0, fiat_percent_change_tv)
        fiat_price_tv.text = currencySymbol + fiatPrice
        currency_tv.text = currency
        price_crypt_tv.text = "$btcPrice BTC"
        fiat_percent_change_tv.text = "($absPercentChange%)"
        market_cap_tv.text = currencySymbol + "$marketCap " + currency
        volume_tv.text = currencySymbol + "$volume " + currency
        rank_tv.text = "Rank: " + coin.rank
    }

    fun showCurrencySelectDialog(){
        val fm = supportFragmentManager
        val frag = SelectGridItemDialogFragment.newInstance("Select Currency",
                resources.getStringArray(R.array.currency_array))
        frag.show(fm, "select_item")
    }

    fun setPercentChangeColor(isPositive: Boolean, textView: TextView){
        if(isPositive)
            textView.setTextColor(ContextCompat.getColor(this, R.color.value_up))
        else
            textView.setTextColor(ContextCompat.getColor(this, R.color.value_down))
    }

    private fun scheduleAlarm(){
        intent = Intent(applicationContext, MyAlarmReceiver::class.java)
        intent.putExtra("id", "raiblocks")
        val pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val firstMillis = System.currentTimeMillis()
        val alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 300000, pIntent)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                IntentFilter(ApiListenerService.BROADCAST_PRICE_CHANGE))
        scheduleAlarm()
        coinUpdate()
    }
}