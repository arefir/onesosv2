package com.example.newonesos

interface CallbackInterface {
    fun resetTimer() {
        println("called")
    }

    fun updateText(time: Long) {
        println("Timer Text Updated to $Long")
    }

    fun updateRecurr(time: Long) {
        println("recur timer : $Long")
    }

    fun resetRecurr() {
        println("recurring reset")
    }

    fun resetRecurringTimer() {

    }

    fun sendSOS() {
        println("SOS sent")
    }
}