package com.elias.belibrary.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.elias.belibrary.R
import com.elias.belibrary.util.*
import kotlinx.android.synthetic.main.activity_beacon_detalle.*
import java.lang.Exception
import java.net.URL

class BeaconDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_detalle)

            try {
                val contenidoAdulto = intent.getBooleanExtra("adulto",true)
                if(contenidoAdulto){
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Este contenido solo es apto para mayores de edad.")
                        .setTitle("¿Eres mayor de edad?")
                        .setPositiveButton("Si"
                        ) { _, _ ->
                            iniciarVista()
                        }
                        .setNegativeButton("No"
                        ) { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                    builder.setCancelable(false)
                    builder.create().show()
                }else{
                    iniciarVista()
                }
            }catch (e : Exception){
                DebugClass.logError("Error crear alerta +18 $e")
            }
    }

    fun iniciarVista() {
        try {
            val urlImagen = intent.getStringExtra("urlImagen")?:""
            val beaconMajor = intent.getStringExtra("beaconMajor")?:""
            val beaconMinor = intent.getStringExtra("beaconMinor")?:""
            val fanid = intent.getStringExtra("findId")?:""
            val userid = intent.getStringExtra("userid")?:""
            val appid = intent.getStringExtra("appid")?:""


            var colorboton = intent.getStringExtra("colorboton")?:""
            var colorfondo = intent.getStringExtra("colorfondo")?:""
            var colortextoboton = intent.getStringExtra("colortextoboton")?:""

            val textoboton = intent.getStringExtra("textoboton")?:""



            if (!colorboton.contains("#")) {
                colorboton = "#${colorboton}"
            }
            if (!colorfondo.contains("#")) {
                colorfondo = "#${colorfondo}"
            }
            if (!colortextoboton.contains("#")) {
                colortextoboton = "#${colortextoboton}"
            }


            fondoDetalle.setBackgroundColor(Color.parseColor(colorfondo))


            val urlWeb = "https://beaconbat.com/bserv-2/resources/ads/click/${fanid}/${userid}/${beaconMajor}/${beaconMinor}/${appid}"

            if (urlImagen.isNotEmpty()) {
                Thread {
                    try {
                        val url = URL(urlImagen)
                        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        runOnUiThread {
                            imageView.setImageBitmap(bmp)
                            progressBar.visibility = View.GONE
                        }
                    }catch (e : Exception){
                        DebugClass.logError("Error imagen $urlImagen")
                    }
                }.start()

            }

            if(urlWeb.isNotEmpty() && textoboton.isNotEmpty()){
                button.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb))
                    startActivity(browserIntent)

                }
                button.setBackgroundColor(Color.parseColor(colorboton))
                button.setTextColor(Color.parseColor(colortextoboton))
                button.text = textoboton
                button.visibility = View.VISIBLE
            }

        }catch (e : Exception){
            DebugClass.logError("Error iniciar vista detalle $e")
        }
    }

}
