package com.elias.betest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.elias.belibrary.MonitoringBeacon

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("algo" , "main open!!!")
        val monitor = MonitoringBeacon(this,"8cbde5be-b52d-4466-875e-bd6a7c308978",null,true)
        monitor.init()
    }
}