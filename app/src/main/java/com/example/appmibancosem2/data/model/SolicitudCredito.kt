package com.example.appmibancosem2.data.model

data class SolicitudCredito(
    val id: Int = 0,
    val monto: Double,
    val plazoMeses: Int,
    val tipo: String,
    val dni: String,
    val estado: String = "pendiente",
    val fechaLocal: String = ""
)