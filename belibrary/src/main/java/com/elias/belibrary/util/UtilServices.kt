package com.elias.belibrary.util

import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class UtilServices : AsyncTask<UtilInterfaceService, Int, Boolean>() {

    override fun doInBackground(vararg params: UtilInterfaceService?): Boolean? {

        val responseInterface = params[0]
        if(responseInterface != null){
            try{
                when(responseInterface.tipoService) {
                    Config.SERVICE_TIPE.GET -> {
                        getService(responseInterface)
                    }
                    Config.SERVICE_TIPE.POST -> {
                        postService(responseInterface)
                    }
                }
            }catch (e : Exception){
            DebugClass.logError("Error service r $e")
            }
        }
        return true
    }


    private fun getService(utilInterfaceService : UtilInterfaceService){
        var urlConnection: HttpURLConnection? = null
         try {
            val urlServicio = utilInterfaceService.urlService

            val url = URL(urlServicio)
            urlConnection  = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            if(!utilInterfaceService.token.isNullOrEmpty()){
                urlConnection.addRequestProperty("Authorization", "Bearer ${utilInterfaceService.token}")
            }
            urlConnection.connectTimeout = 30000
            urlConnection.connect()

            var buffer = ""

            if(urlConnection.responseCode == HttpURLConnection.HTTP_OK || urlConnection.responseCode == HttpURLConnection.HTTP_CREATED ){
                try {
                    val inputStream: InputStream = urlConnection.inputStream
                    buffer = inputStream.bufferedReader().use(BufferedReader::readText)
                }catch (e : Exception){
                   DebugClass.logError( "Error inputStream.bufferedReader " + urlConnection.responseCode)
                }
                utilInterfaceService.exito(buffer)

            }else{
                try {
                    val errorStreamIn: InputStream = urlConnection.errorStream
                    buffer = errorStreamIn.bufferedReader().use(BufferedReader::readText)
                }catch (e : Exception){
                    DebugClass.logError( "Error errorStreamIn.bufferedReader " + urlConnection.responseCode)
                }

                utilInterfaceService.error(buffer)

            }

        }catch (e : IOException){
            DebugClass.logError( "Error IOException ${e.localizedMessage}")
            //ERROR
            utilInterfaceService.error("" + urlConnection!!.responseCode)

        } finally {
            urlConnection?.disconnect()
        }

    }


    private fun postService(utilInterfaceService : UtilInterfaceService){
        var urlConnection: HttpURLConnection? = null
         try {
            val urlServicio = utilInterfaceService.urlService
            val url = URL(urlServicio)
            urlConnection  = url.openConnection() as HttpsURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.connectTimeout = 30000
            if(utilInterfaceService.token != ""){
                urlConnection.addRequestProperty("Authorization", "Bearer ${utilInterfaceService.token}")
            }
            urlConnection.setRequestProperty("Content-Type", "application/json")
            if(utilInterfaceService.extraHeaders != null){
                for(i in utilInterfaceService.extraHeaders!!.keys){
                    urlConnection.setRequestProperty(i, utilInterfaceService.extraHeaders!![i])
                }
            }

            urlConnection.setUseCaches(false)
            if(utilInterfaceService.paramService != null){
                val wr = OutputStreamWriter(urlConnection.getOutputStream())
                wr.write(utilInterfaceService.paramService.toString())
                wr.flush()
                wr.close()
            }

            urlConnection.connect()

            var buffer = ""
            if(urlConnection.responseCode == HttpURLConnection.HTTP_OK || urlConnection.responseCode == HttpURLConnection.HTTP_CREATED ){
                try {
                    val inputStream: InputStream = urlConnection.inputStream
                    buffer = inputStream.bufferedReader().use(BufferedReader::readText)
                }catch (e : Exception){
                    DebugClass.logError( "Error inputStream.bufferedReader " + urlConnection.responseCode)
                }
                utilInterfaceService.exito(buffer)

            }else{
                try {
                    val errorStreamIn: InputStream = urlConnection.errorStream
                    buffer = errorStreamIn.bufferedReader().use(BufferedReader::readText)
                }catch (e : Exception){
                    DebugClass.logError( "Error errorStreamIn.bufferedReader " + urlConnection.responseCode)
                }
                utilInterfaceService.error(buffer)
            }

        }catch (e : IOException){
            DebugClass.logError( "Error IOException ${e.localizedMessage}")

        } finally {
            urlConnection?.disconnect()
        }
    }

}