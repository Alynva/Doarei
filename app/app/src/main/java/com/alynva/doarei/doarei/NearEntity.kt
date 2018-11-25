package com.alynva.doarei.doarei

import java.io.Serializable

data class NearEntity (var nome: String,
                       val desc: String,
                       val adress: String,
                       val distancia: String,
                       val angulo: Double,
                       val photoPath: String? = null) : Serializable