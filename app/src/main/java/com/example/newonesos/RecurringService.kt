package com.example.newonesos

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.Toast
import java.util.Timer
import java.util.TimerTask


class RecurringService: Service() {
    private val timer:Timer = Timer()
    private var rootIntent: Intent? = null
    var countdown: CountDownTimer? = null
    var mReceiver: BroadcastReceiver? = null
    private val binder = LocalBinder()
    private var serviceCallbacks: CallbackInterface? = null
    private var timeValue: Long? = null


    inner class LocalBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        fun getService(): RecurringService = this@RecurringService
    }

    inner class MainTask : TimerTask() {
        override fun run() {
            println("timer ended")
            rootIntent?.let { this@RecurringService.onTaskRemoved(it) }
        }
    }

    override fun onCreate() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        mReceiver = ScreenReceiver()
        registerReceiver(mReceiver, filter)
        println("service created")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("recurring service started")

        @Suppress("DEPRECATION")
        val time = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent?.getSerializableExtra("time")
        else
            intent?.getSerializableExtra("time") as Long
        if (time != null) {
            rootIntent = intent
            timeValue = time as Long

            countdown = object: CountDownTimer(timeValue!!, 1_000) {
                override fun onTick(remaining: Long) {
                    serviceCallbacks?.updateRecurr(remaining)
                }

                override fun onFinish() {
                    println("finished")
                    serviceCallbacks?.sendSOS()
                    startService(intent)
                }
            }.start()
        }

        if (time == null) {
            val screenOn = intent?.getBooleanExtra("screen_state", false)
            if (!screenOn!!) {
                println("ScreenOn")
                countdown?.cancel()
                startService(rootIntent)
                // YOUR CODE
            } else {
                println("ScreenOff")
                // YOUR CODE
            }

        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onTaskRemoved(rootIntent: Intent) {

    }

    override fun onDestroy() {
        countdown?.cancel()
        unregisterReceiver(mReceiver)
        stopSelf()
    }

    fun setCallbacks(callbacks: CallbackInterface?) {
        serviceCallbacks = callbacks
    }


}