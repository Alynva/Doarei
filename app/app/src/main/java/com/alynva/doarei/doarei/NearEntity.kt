package com.alynva.doarei.doarei

import java.io.Serializable

data class NearEntity (var nome: String,
                       val desc: String,
                       val adress: String,
                       val distancia: String,
                       val angulo: Double,
                       val fb: String = "",
                       val tt: String = "",
                       val li: String = "",
                       val photoPath: String = "") : Serializable