package com.example.newonesos

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.Toast
import java.io.Serializable
import java.util.Timer
import java.util.TimerTask


class TimerService: Service() {
    private val timer:Timer = Timer()
    private var rootIntent: Intent? = null
    var countdown: CountDownTimer? = null
    private val binder = LocalBinder()
    private var serviceCallbacks: CallbackInterface? = null


    inner class LocalBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        fun getService(): TimerService = this@TimerService
    }

    inner class MainTask : TimerTask() {
        override fun run() {
            println("timer ended")
            rootIntent?.let { this@TimerService.onTaskRemoved(it) }
        }
    }

    private fun startCountdown(timer:Long) {
        countdown = object: CountDownTimer(timer, 1_000) {
            override fun onTick(remaining: Long) {
                serviceCallbacks?.updateText(remaining)
            }

            override fun onFinish() {
                println("finished")
                serviceCallbacks?.sendSOS()
            }
        }.start()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("timer service started")
        rootIntent = intent
        @Suppress("DEPRECATION")
        val time = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent?.getSerializableExtra("time")
        else
            intent?.getSerializableExtra("time") as Long
        if (time != null) {
            startCountdown(time as Long)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        stopService(rootIntent)
    }

    fun setCallbacks(callbacks: CallbackInterface?) {
        serviceCallbacks = callbacks
    }

}