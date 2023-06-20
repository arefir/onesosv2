package com.example.newonesos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.newonesos.databinding.ActivityAddContactBinding
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.Exception

class AddContactActivity : ComponentActivity() {
    lateinit var binding: ActivityAddContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        binding.addbtn.setOnClickListener {
            val name = binding.name.text.toString()
            val number = binding.number.text.toString()
//            val file = File("contacts.txt")
//            val fileOutputStream = FileOutputStream(file, true)
//            fileOutputStream.write(name.toByteArray())
//            fileOutputStream.write(number.toByteArray())
//            fileOutputStream.close()

            val output = PrintStream(openFileOutput("contacts.txt", Context.MODE_APPEND))
            output.println(name)
            output.println(number)
            output.close()
            val intent = Intent()
            intent.putExtra("contact", Contact(name, number))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.cancelbtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}