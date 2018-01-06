package com.llamalabb.com.raipriceviewer.coindetails

import android.app.AlarmManager
import android.app.FragmentManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.widget.TextView
import com.llamalabb.com.raipriceviewer.*
import com.llamalabb.com.raipriceviewer.R
import com.llamalabb.com.raipriceviewer.model.AppDatabase
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin
import com.llamalabb.com.raipriceviewer.model.CoinMarketCapCoin_Table
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
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
    }

    override fun itemClicked(text: String) {
        currency_select_button.text = text

        val prev = supportFragmentManager.findFragmentByTag("select_item")
        prev?.let{ (prev as DialogFragment).dismiss() }
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

    private fun initDatabase(){
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(AppDatabase::class.java)
                        .databaseName("AppDatabase")
                        .build())
                .build())
    }

    private fun coinUpdate(){
        initDatabase()
        val coin: CoinMarketCapCoin? = SQLite.select()
                .from(CoinMarketCapCoin::class)
                .where(CoinMarketCapCoin_Table.id.eq("raiblocks"))
                .querySingle()
        coin?.let {showCoinInfo(it)}
    }



    fun showCoinInfo(coin: CoinMarketCapCoin){
        val percentChange = coin.percent_change_24h.toDouble()
        val absPercentChange = percentChange.toTwoDecimalPlacesAbs()
        val fiatPrice = coin.price_usd.toDouble().toTwoDecimalPlaces()
        val marketCap = coin.market_cap_usd.formatNumber()
        val volume = coin.volume_usd.formatNumber()
        val btcPrice = coin.price_btc
        setPercentChangeColor(percentChange >= 0, fiat_percent_change_tv)
        fiat_price_tv.text = "$$fiatPrice"
        price_crypt_tv.text = "$btcPrice BTC"
        fiat_percent_change_tv.text = "($absPercentChange%)"
        market_cap_tv.text = "$$marketCap USD"
        volume_tv.text = "$$volume USD"
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
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 60000, pIntent)
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