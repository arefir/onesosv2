package com.example.newonesos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.material3.contentColorFor


class ScreenReceiver : BroadcastReceiver() {
    private var screenOff = false

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            screenOff = true
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            screenOff = false
        }
        val i = Intent(context, RecurringService::class.java)
        i.putExtra("screen_state", screenOff)
        context.startService(i)
    }
}