package com.alynva.doarei.doarei

import java.io.Serializable

/**
 * Created by Alynva on 28/10/2018.
 */

data class NearEntity (var nome: String,
                       val desc: String,
                       val adress: String,
                       val photoPath: String? = null) : Serializable