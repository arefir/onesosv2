package com.example.newonesos

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newonesos.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.PrintStream
import java.io.Serializable
import java.util.Scanner
import java.util.Timer

class MainActivity: ComponentActivity(), CallbackInterface {
    val RECORD_REQUEST_CODE = 101
    lateinit var binding: ActivityMainBinding
//    private lateinit var uService: UpdateService
    private lateinit var tService: TimerService
    private lateinit var rService: RecurringService
//    private var uBound: Boolean = false
    private var tBound: Boolean = false
    private var rBound: Boolean = false
    var contactList:ArrayList<Contact> = ArrayList()
    lateinit var adapter: ContactAdapter
    var sharedPref: SharedPreferences? = null

    //service binding
//    private val uConnection = object : ServiceConnection {
//
//        override fun onServiceConnected(className: ComponentName, service: IBinder) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance.
//            val binder = service as UpdateService.LocalBinder
//            uService = binder.getService()
//            uBound = true
//            uService.setCallbacks(this@MainActivity)
//        }
//
//        override fun onServiceDisconnected(arg0: ComponentName) {
//            uBound = false
//        }
//    }

    private val tConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as TimerService.LocalBinder
            tService = binder.getService()
            tBound = true
            tService.setCallbacks(this@MainActivity)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            tBound = false
        }
    }

    private val rConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as RecurringService.LocalBinder
            rService = binder.getService()
            rBound = true
            rService.setCallbacks(this@MainActivity)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            rBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("sosText", Context.MODE_PRIVATE);

        binding.startButton.setOnClickListener{ startAction() }
        binding.resetButton.setOnClickListener{ resetAction() }
        binding.startButton2.setOnClickListener { startRecurring() }
        binding.stopButton.setOnClickListener { stopRecurring() }
        binding.ctclistButton.setOnClickListener {
            val intent = Intent(this, ContactListActivity::class.java)
            startActivity(intent)
        }
        binding.saveButton.setOnClickListener { setSOSMessage() }
        initData()
    }

    override fun onStart() {
        super.onStart()

        contactList.clear()
        initData()
        initPermissions()
        binding.sosMessageText.setText(sharedPref?.getString("sosText", "default if empty"))
    }

    fun refreshList() {
        contactList.clear()
        initData()
    }

    private fun readFileScan(scan:Scanner) {
        while(scan.hasNextLine()){
            val name = scan.nextLine()
            val number = scan.nextLine()
            contactList.add(Contact(name, number))
        }
    }

    fun initData(){
        try {
            val scan2 = Scanner(openFileInput("contacts.txt"))
            readFileScan(scan2)
        } catch (e: FileNotFoundException) {

        }
//        val scan = Scanner(resources.openRawResource(R.raw.contacts))
//        readFileScan(scan)
    }

    fun initPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)

        val tag = "SMS Permission"
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(tag, "Permission to record denied")
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_PHONE_STATE),
                RECORD_REQUEST_CODE)

        }
    }


    private fun getInput(textField: EditText): Int {
        val input = textField.text.toString()
        val splitInput = input.split(":")
        for (text in splitInput) {
            println(text)
        }

        if (splitInput.size < 4 ) {
            return 0
        }

        val days = splitInput[0].toInt() * 24 * 60 * 60 * 1000
        val hours = splitInput[1].toInt() * 60 * 60 * 1000
        val minutes = splitInput[2].toInt() * 60 * 1000
        val seconds = splitInput[3].toInt() * 1000

        return days + hours + minutes + seconds
    }

    fun startAction() {
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, tConnection, Context.BIND_AUTO_CREATE)
            println("tBOUND")
        }

        val time = getInput(binding.timerText).toLong()
        if (time <= 0) {
            binding.timerText.setText(R.string.place_holder_time)
            Toast.makeText(this, "Please Input A Valid Duration DD:HH:MM:SS", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("time", time)
        intent.putExtra("list", contactList)
        startService(intent)
    }

    fun resetAction() {
        unbindService(tConnection)
        val intent = Intent(this, TimerService::class.java)
        stopService(intent)
        binding.timerText.setText(R.string.place_holder_time)
    }

    override fun updateText(time: Long) {
        var remaining = time.toDouble()/1000

        var temp = remaining/60 - (remaining/60).toLong()
        remaining /= 60
        val seconds = (temp*60).toInt()

        temp = remaining/60 - (remaining/60).toLong()
        remaining /= 60
        val minutes = (temp*60).toInt()

        temp = remaining/24 - (remaining/24).toLong()
        remaining /=24
        val hours = (temp*24).toInt()

        val days = (remaining - remaining.toLong()).toInt()

        val text = "${if (days < 10) "0$days" else days }:${if (hours < 10) "0$hours" else hours }:${if (minutes < 10) "0$minutes" else minutes }:${if (seconds < 10) "0$seconds" else seconds }"
        binding.timerText.setText(text)

        super.updateText(time)
    }

    private fun startRecurring() {
        Intent(this, RecurringService::class.java).also { intent ->
            bindService(intent, rConnection, Context.BIND_AUTO_CREATE)
            println("rBOUND")
        }

        val time = getInput(binding.timerText2).toLong()
        if (time <= 0) {
            binding.timerText2.setText(R.string.place_holder_time)
            Toast.makeText(this, "Please Input A Valid Duration DD:HH:MM:SS", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, RecurringService::class.java)
        intent.putExtra("time", time)
        intent.putExtra("list", contactList)
        startService(intent)
    }

    private fun stopRecurring() {
        unbindService(rConnection)
        val intent = Intent(this, RecurringService::class.java)
        stopService(intent)
    }

    fun setSOSMessage() {
        val input = binding.sosMessageText.text
        sharedPref?.edit()?.putString("sosText", input.toString())?.commit()
    }

    override fun sendSOS() {
        try {
            if (contactList.size > 0) {
                val smsManager: SmsManager = applicationContext.getSystemService(SmsManager::class.java)

                // on below line we are sending text message.
                for (contact in contactList) {
                    smsManager.sendTextMessage(contact.number, null, sharedPref?.getString("sosText", "default if empty"), null, null)
                }

                // on below line we are displaying a toast message for message send,
                Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()
            }


        } catch (e: Exception) {

            // on catch block we are displaying toast message for error.
            Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG)
                .show()
            println(e.message.toString())
        }
    }


}