package com.alynva.doarei.doarei

import java.io.Serializable

data class NearEntity (var nome: String,
                       val desc: String,
                       val adress: String,
                       val photoPath: String? = null) : Serializable