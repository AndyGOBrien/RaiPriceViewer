package com.llamalabb.com.raipriceviewer

import android.app.Application
import com.llamalabb.com.raipriceviewer.model.AppDatabase
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager

/**
 * Created by andy on 1/3/18.
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(AppDatabase::class.java)
                        .databaseName("AppDatabase")
                        .build())
                .build())
    }
}