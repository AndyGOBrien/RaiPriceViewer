package com.llamalabb.com.raipriceviewer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.llamalabb.com.raipriceviewer.service.background.MyAlarmReceiver

/**
 * Created by andy on 1/26/18.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            scheduleAlarm(context)
        }
    }

    private fun scheduleAlarm(context: Context?) {
        val i = Intent(context, MyAlarmReceiver::class.java)
        i.putExtra("id", "raiblocks")
        i.action = MyAlarmReceiver.ACTION
        val pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarm = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 30000, 300000, pIntent)
    }

}