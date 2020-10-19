package com.elias.belibrary.util

import org.json.JSONObject

interface  UtilInterfaceService {

    var urlService : String
    var tipoService : Config.SERVICE_TIPE
    var paramService : JSONObject?
    var extraHeaders : HashMap<String,String>?
    var tokenType : String?
    var token : String?
    var user : String
    var password : String


    fun exito(respuesta :String)

    fun error(codigoError : String)



}