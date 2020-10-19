package com.elias.belibrary

import android.content.Context
import android.content.Intent
import com.elias.belibrary.ui.ResultadoActivity
import com.elias.belibrary.util.ActivityUtil
import com.elias.belibrary.util.DebugClass
import com.elias.belibrary.util.Util
import java.lang.Exception


import java.util.*

class MonitoringBeacon (val context : Context, val appId : String, val userId : String?, val debug : Boolean){

    fun init(){
        DebugClass.debugMode = debug
        try {
            DebugClass.logInfo("Iniciando...")
            ActivityUtil.noIniciado = false
            val irMonitoring = Intent(context, ResultadoActivity::class.java)
            irMonitoring.putExtra("appId", appId)
            var uudiUser = userId
            if (uudiUser.isNullOrEmpty()) {
                val sharedPreferences = context.getSharedPreferences("PRE", Context.MODE_PRIVATE)
                if (sharedPreferences.getString("ble_uuiduser", "").isNullOrEmpty()) {
                    DebugClass.logInfo("userId es nulo o vacio")
                    DebugClass.logInfo("generando un userId")
                    val edit = sharedPreferences.edit()
                    uudiUser = UUID.randomUUID().toString()
                    edit.putString("ble_uuiduser", uudiUser)
                    edit.apply()
                } else {
                    uudiUser = sharedPreferences.getString("ble_uuiduser", "")
                }
            }
            ActivityUtil.utilView = Util(context, appId, uudiUser!!)
            context.startActivity(irMonitoring)
        }catch (e : Exception){
            DebugClass.logError("ERROR MonitoringBeacon - ${e.cause}")
        }
    }


}