package com.elias.belibrary.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.elias.belibrary.R
import com.elias.belibrary.util.*
import org.altbeacon.beacon.*
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule
import kotlin.random.Random

class ResultadoActivity : AppCompatActivity(), BeaconConsumer {

    var beaconManager : BeaconManager? = null
    var appId = ""
    val regionRating = Region("myRangegUniqueId", Identifier.fromUuid(UUID.fromString("1968922F-A4E0-455E-BCB7-89BD3EB92CF7")), null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)
        appId = intent.getStringExtra("appId")?:""

        permisos()
    }


    fun permisos(){
        DebugClass.logInfo("Solicitando permisos ")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((this), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1033)
            } else {
                startScan()
            }
        }else{
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((this), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1033)
            } else {
                startScan()
            }
        }


    }


    fun startScan(){
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager!!.backgroundMode = true
        beaconManager!!.getBeaconParsers().add(BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
        beaconManager!!.bind(this)
    }





    override fun onBeaconServiceConnect() {
        beaconManager!!.removeAllMonitorNotifiers()
        beaconManager!!.addMonitorNotifier(object : MonitorNotifier{
            override fun didDetermineStateForRegion(p0: Int, p1: Region?) {

            }
            override fun didEnterRegion(region: Region?) {
            }
            override fun didExitRegion(p0: Region?) {

            }
        })
        val notifier = RangeNotifier { mutableCollection, _ ->

            if(!mutableCollection.isEmpty()){
                var major = ""
                var menor = ""
                val beacon = mutableCollection.iterator().next()
                DebugClass.logInfo("id1          ${beacon.id1}")

                try {
                    DebugClass.logInfo("id2          ${beacon.id2}")
                    major = beacon.id2.toString()
                    DebugClass.logInfo("id3          ${beacon.id3}")
                    menor  = beacon.id3.toString()
                }catch (e : Exception){
                    DebugClass.logError("Error onBeaconServiceConnect - id2-id3 $major - $menor")
                }
                DebugClass.logInfo("manufacturer             ${beacon.manufacturer}")
                DebugClass.logInfo("identifiers              ${beacon.identifiers}")
                DebugClass.logInfo("-----dt-------")

                if(ActivityUtil.listaBeaconEncontrados.isEmpty()){
                    startTimer()
                }
                val idBeacon = "${beacon.id1}_${major}_${menor}"
                if(!ActivityUtil.listaBeaconEncontrados.contains(idBeacon)){
                    ActivityUtil.listaBeaconEncontrados.add(idBeacon)
                    servicioConsulta(ActivityUtil.utilView!!.appId, major, menor, ActivityUtil.utilView!!.userId)
                }

            }



        }
        beaconManager!!.addRangeNotifier(notifier)
        beaconManager!!.startRangingBeaconsInRegion(regionRating)
        DebugClass.logInfo("startRangingBeacon")
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1033){
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan()
            }
        }
    }



    fun servicioConsulta(appId : String, major : String, menor : String, userId : String){


        val srv = object : UtilInterfaceService {
            override var urlService: String = "${ActivityUtil.apiEncontrado}/${appId}/${major}/${menor}/${userId}"
            override var tipoService =  Config.SERVICE_TIPE.GET
            override var paramService: JSONObject? = null
            override var extraHeaders : HashMap<String,String>? = null
            override var tokenType: String? = "Bearer"
            override var token: String? = ""
            override var user: String = ""
            override var password: String = ""

            override fun exito(respuesta: String) {
                val json = JSONObject(respuesta)

                val urlWeb = json.getString("urldestino")
                val urlImagen = json.getString("urlfondo")
                val titulo = json.getString("titulo")
                val cuerpo = json.getString("cuerpo")
                val adulto = json.getBoolean("adultos")
                val findId = json.getString("fand")
                val textoboton = json.getString("textoboton")

                val colorboton = json.getString("colorboton")
                val colorfondo = json.getString("colorfondo")
                val colortextoboton = json.getString("colortextoboton")


                crearNotificacion(urlWeb , urlImagen,titulo,cuerpo,findId,adulto,major,menor,userId,appId,colorboton,colorfondo,colortextoboton,textoboton)

            }

            override fun error(codigoError: String) {
                    DebugClass.logError("Error servicio [$codigoError]  $urlService")
            }
        }
        UtilServices().execute(srv)
    }


    fun crearNotificacion(urlWeb : String , urlImagen : String,titulo : String,cuerpo : String,
                          findId : String,adulto : Boolean, beaconMajor : String, beaconMinor : String,
                          userid : String,appid : String,colorboton : String,colorfondo : String,colortextoboton : String, textoboton : String){

        val irDetalle = Intent(ActivityUtil.utilView!!.context, BeaconDetalleActivity::class.java)

        irDetalle.putExtra("urlWeb",urlWeb)
        irDetalle.putExtra("urlImagen",urlImagen)
        irDetalle.putExtra("findId",findId)
        irDetalle.putExtra("adulto",adulto)

        irDetalle.putExtra("beaconMajor",beaconMajor)
        irDetalle.putExtra("beaconMinor",beaconMinor)

        irDetalle.putExtra("userid",userid)
        irDetalle.putExtra("appid",appid)

        irDetalle.putExtra("colorboton",colorboton)
        irDetalle.putExtra("colorfondo",colorfondo)
        irDetalle.putExtra("colortextoboton",colortextoboton)
        irDetalle.putExtra("textoboton",textoboton)

        irDetalle.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pm = ActivityUtil.utilView!!.context.packageManager


        val irMainActivity = pm.getLaunchIntentForPackage(ActivityUtil.utilView!!.context.getPackageName())

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(ActivityUtil.utilView!!.context).run {

            addNextIntentWithParentStack(irMainActivity!!)
            getPendingIntent(1027, PendingIntent.FLAG_UPDATE_CURRENT)

            addNextIntentWithParentStack(irDetalle)
            getPendingIntent(1028, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(ActivityUtil.utilView!!.context, "001")
            .setSmallIcon(R.drawable.iconpng)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setContentIntent(resultPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(ActivityUtil.utilView!!.context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channelId = "channel_id"
            val channel = NotificationChannel(
                channelId,
                "Channel readable",
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }

        notificationManager.notify(Random.nextInt(0, 1000), builder.build())
    }


  private fun startTimer(){
        Timer("SettingUp", false).schedule(10000) {
            runOnUiThread{
                ActivityUtil.listaBeaconEncontrados.clear()
            }
        }
    }
}
