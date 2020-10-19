package com.elias.belibrary.util

import android.util.Log

object DebugClass {
    val TAG = "BeDebug"
    var debugMode = false
    fun logInfo(msg : String){
        if(debugMode){
         Log.i(TAG,msg)
        }
    }
    fun logError(msg : String){
        if(debugMode){
            Log.e(TAG,msg)
        }
    }
}