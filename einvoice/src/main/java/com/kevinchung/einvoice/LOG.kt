package com.kevinchung.einvoice

import android.util.Log

object LOG {
    private const val TAG = "einv"
    var enableLog = false

    fun d(msg: String) {
        if(isRunningTest)
            print(msg)
        else if(enableLog)
            Log.d(TAG,msg)
    }

    fun e(msg: String) {
        if(isRunningTest)
            print(msg)
        else if(enableLog)
            Log.e(TAG,msg)
    }
    fun w(msg: String) {
        if(isRunningTest)
            print(msg)
        else if(enableLog)
            Log.w(TAG,msg)
    }
    val isRunningTest : Boolean by lazy {
        try {
            Class.forName("android.support.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}