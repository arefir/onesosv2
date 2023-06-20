package com.example.newonesos

import android.content.Context
import java.io.FileNotFoundException
import java.util.*

class DataHelper constructor(context: Context) {
    private var data: ArrayList<Contact> = ArrayList()
    val context = context

    private fun readFileScan(scan:Scanner) {
        while(scan.hasNextLine()){
            val name = scan.nextLine()
            val number = scan.nextLine()
            data.add(Contact(name, number))
        }
    }

    fun initData(){
        try {
            val scan2 = Scanner(context.openFileInput("contacts.txt"))
            readFileScan(scan2)
        } catch (e: FileNotFoundException) {

        }
//        val scan = Scanner(resources.openRawResource(R.raw.contacts))
//        readFileScan(scan)
    }

    fun getData(): ArrayList<Contact>? {
        return data
    }

    fun setData(data: ArrayList<Contact>?) {
        if (data != null) {
            this.data = data
        }
    }

    private val holder: DataHelper = DataHelper(context)
    fun getInstance(): DataHelper {
        return holder
    }
}