package com.example.appmibancosem2.ui.screens

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogManager {
    private const val NOMBRE_ARCHIVO = "historial_solicitudes.txt"

    fun registrar(contexto: Context, detalle: String) {
        val archivo = File(contexto.filesDir, NOMBRE_ARCHIVO)
        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        archivo.appendText("[$timestamp] $detalle\n")
    }

    fun obtenerHistorial(contexto: Context): String {
        val archivo = File(contexto.filesDir, NOMBRE_ARCHIVO)
        return if (archivo.exists() && archivo.length() > 0) {
            archivo.readText()
        } else {
            "Aún no hay solicitudes registradas."
        }
    }

    fun limpiarHistorial(contexto: Context) {
        val archivo = File(contexto.filesDir, NOMBRE_ARCHIVO)
        if (archivo.exists()) archivo.delete()
    }
}