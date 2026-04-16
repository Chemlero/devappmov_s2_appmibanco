package com.example.appmibancosem2.data.model

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

private val peLocale = Locale("es", "PE")
private val currencyFormatter: NumberFormat = NumberFormat.getNumberInstance(peLocale).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

data class Cuenta(
    val numero: String,
    val tipo: String,
    val saldo: Double,
    val titular: String
) {
    fun numeroEnmascarado(): String {
        val digitos = numero.filter { it.isDigit() }
        val ultimos = digitos.takeLast(4).padStart(4, '0')
        return "****$ultimos"
    }
}

data class Transaccion(
    val descripcion: String,
    val fecha: String,
    val monto: Double,
    val categoria: String = ""
) {
    fun esDebito() = monto < 0
    fun montoAbsoluto() = abs(monto)
    fun montoFormateado(): String = "S/ ${currencyFormatter.format(montoAbsoluto())}"
    fun montoConSigno(): String = (if (esDebito()) "- " else "+ ") + montoFormateado()
}

data class Servicio(val nombre: String, val icono: String = "")

data class ProyeccionAhorroMes(
    val numeroMes: Int,
    val deposito: Double,
    val interes: Double,
    val saldoProyectado: Double
)

data class CuotaDetalle(
    val mes: Int,
    val cuota: Double,
    val capital: Double,
    val interes: Double,
    val saldoPendiente: Double
)

data class CuentaAhorro(
    val nombre: String,
    val saldo: Double,
    val meta: Double,
    val plazo: String,
    val tasaAnual: Double,
    val depositoMensual: Double
) {
    fun porcentaje() = (saldo / meta).coerceIn(0.0, 1.0).toFloat()
    fun faltaParaMeta() = (meta - saldo).coerceAtLeast(0.0)

    fun calcularMesesParaMeta(): Int {
        val tasaMensual = tasaAnual / 100.0 / 12.0
        var saldoActual = saldo
        var meses = 0
        while (saldoActual < meta && meses < 360) {
            val interes = saldoActual * tasaMensual
            saldoActual += interes + depositoMensual
            meses++
        }
        return meses
    }

    fun mensajeMotivacional(): String {
        val progreso = porcentaje()
        return when {
            progreso > 0.75f -> "¡Casi lo logras!"
            progreso > 0.50f -> "¡Vas muy bien!"
            progreso > 0.25f -> "¡Buen comienzo!"
            else -> "Cada sol cuenta"
        }
    }

    fun fechaMetaAproximada(): String {
        val meses = calcularMesesParaMeta()
        val fecha = LocalDate.now().plusMonths(meses.toLong())
        val mes = fecha.month.getDisplayName(TextStyle.FULL, peLocale)
        return "${mes.replaceFirstChar { it.titlecase(peLocale) }} ${fecha.year}"
    }

    fun proyeccionSeisMeses(): List<ProyeccionAhorroMes> {
        val tasaMensual = tasaAnual / 100.0 / 12.0
        var saldoActual = saldo
        return (1..6).map { mes ->
            val interes = saldoActual * tasaMensual
            saldoActual += interes + depositoMensual
            ProyeccionAhorroMes(mes, depositoMensual, interes, saldoActual)
        }
    }
}

data class SimuladorPrestamo(
    val monto: Double,
    val tasaAnual: Double,
    val cuotas: Int
) {
    fun calcularCuota(): Double {
        val r = tasaAnual / 12.0 / 100.0
        if (r == 0.0) return monto / cuotas
        val factor = (1 + r).pow(cuotas.toDouble())
        return monto * (r * factor) / (factor - 1)
    }

    fun totalAPagar(): Double = calcularCuota() * cuotas
    fun interesesTotales(): Double = totalAPagar() - monto

    fun primerasCuotas(limite: Int = 6): List<CuotaDetalle> {
        val cuota = calcularCuota()
        val r = tasaAnual / 12.0 / 100.0
        var saldoPendiente = monto
        return (1..min(limite, cuotas)).map { mes ->
            val interesMes = saldoPendiente * r
            val capital = cuota - interesMes
            saldoPendiente = (saldoPendiente - capital).coerceAtLeast(0.0)
            CuotaDetalle(mes, cuota, capital, interesMes, saldoPendiente)
        }
    }
}

object DemoData {
    val cuentas = listOf(
        Cuenta(
            numero = "00194521",
            tipo = "Cuenta Corriente",
            saldo = 4250.00,
            titular = "Carlos Lopez"
        ),
        Cuenta(
            numero = "00278134",
            tipo = "Cuenta Ahorro",
            saldo = 12875.50,
            titular = "Carlos Lopez"
        )
    )

    val cuenta get() = cuentas.first()

    val transacciones = listOf(
        Transaccion("Pago Agua SEDAPAL", "16/04/2026", -85.00, "Servicios"),
        Transaccion("Depósito Sueldo", "15/04/2026", 3500.00, "Ingresos"),
        Transaccion("Netflix Cable", "14/04/2026", -49.90, "Entretenimiento"),
        Transaccion("Transferencia recibida", "13/04/2026", 500.00, "Transferencias"),
        Transaccion("Supermercado Wong", "12/04/2026", -185.60, "Compras"),
        Transaccion("Interés mensual", "10/04/2026", 37.25, "Ahorro"),
        Transaccion("Pago préstamo", "09/04/2026", -472.89, "Préstamos"),
        Transaccion("Yape recibido", "08/04/2026", 120.00, "Transferencias")
    )

    val servicios = listOf(
        Servicio("Agua (SEDAPAL)"),
        Servicio("Luz (ENEL)"),
        Servicio("Cable / Streaming"),
        Servicio("Internet"),
        Servicio("Telefonía móvil"),
        Servicio("Préstamo personal")
    )

    val cuentaAhorro = CuentaAhorro(
        nombre = "Meta Viaje Europa",
        saldo = 12875.50,
        meta = 20000.00,
        plazo = "Meta 2027",
        tasaAnual = 3.5,
        depositoMensual = 500.00
    )
}

fun Double.formatoMoneda(): String = "S/ ${currencyFormatter.format(this)}"
