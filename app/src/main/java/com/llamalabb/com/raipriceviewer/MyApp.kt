package com.llamalabb.com.raipriceviewer

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.llamalabb.com.raipriceviewer.model.AppDatabase
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager

/**
 * Created by andy on 1/22/18.
 */
class MyApp : Application() {

    companion object {
        lateinit var settings: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        settings = this.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        initDatabase()
    }
    private fun initDatabase(){
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(AppDatabase::class.java)
                        .databaseName("AppDatabase")
                        .build())
                .build())
    }
}