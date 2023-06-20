//package com.example.newonesos
//
//import android.app.Service
//import android.content.BroadcastReceiver
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Binder
//import android.os.IBinder
//
//
//class UpdateService : Service() {
//    private val binder = LocalBinder()
//    private var serviceCallbacks: CallbackInterface? = null
//
//
//    inner class LocalBinder : Binder() {
//        // Return this instance of MyService so clients can call public methods
//        fun getService(): UpdateService = this@UpdateService
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
//        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
//        filter.addAction(Intent.ACTION_SCREEN_OFF)
//        val mReceiver: BroadcastReceiver = ScreenReceiver()
//        registerReceiver(mReceiver, filter)
//        println("service created")
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        println("service started")
//        val screenOn = intent?.getBooleanExtra("screen_state", false)
//        if (!screenOn!!) {
//            println("ScreenOn")
//            serviceCallbacks?.resetTimer()
//            // YOUR CODE
//        } else {
//            println("ScreenOff")
//            // YOUR CODE
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    fun restartRecurr() {
//        val intent = Intent(applicationContext, RecurringService::class.java)
//        stopService(intent)
//        startService(intent)
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return binder
//    }
//
//    fun setCallbacks(callbacks: CallbackInterface?) {
//        serviceCallbacks = callbacks
//    }
//}