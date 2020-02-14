package com.anodev.fullwebview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager


class AutoBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Initialize preferences
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            // Load autoboot bool from config
            val state = sharedPref.getBoolean(context.
                getString(R.string.app_auto_start_settings_key), false)
            if (state){
                val activityIntent = Intent(context, FullscreenActivity::class.java)
                activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(activityIntent)
            }
        }
    }
}