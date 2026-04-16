package com.example.appmibancosem2.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.ui.theme.*
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SolicitudCreditoScreen(onBack: () -> Unit) {
    val contexto = LocalContext.current
    var monto by remember { mutableStateOf("") }
    var plazo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var mostrarDialogoEnvio by remember { mutableStateOf(false) }
    var mostrarDialogoHistorial by remember { mutableStateOf(false) }
    var mostrarDialogoBorrador by remember { mutableStateOf(false) }
    var mostrarDialogoLimpiarHis by remember { mutableStateOf(false) }
    var textoHistorial by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val prefs = contexto.getSharedPreferences("borrador_solicitud", Context.MODE_PRIVATE)
        monto = prefs.getString("sol_monto", "") ?: ""
        plazo = prefs.getString("sol_plazo", "") ?: ""
        tipo = prefs.getString("sol_tipo", "") ?: ""
        dni = prefs.getString("sol_dni", "") ?: ""
    }

    fun guardar(clave: String, valor: String) {
        contexto.getSharedPreferences("borrador_solicitud", Context.MODE_PRIVATE)
            .edit()
            .putString(clave, valor)
            .apply()
    }

    fun limpiarBorrador() {
        contexto.getSharedPreferences("borrador_solicitud", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        monto = ""
        plazo = ""
        tipo = ""
        dni = ""
    }

    Scaffold(
        topBar = {
            MiBancoTopBar(
                titulo = "Solicitud de Crédito",
                mostrarBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Los campos se guardan automáticamente",
                fontSize = 12.sp,
                color = GoldAccent
            )

            CampoSolicitud(
                label = "Monto solicitado (S/)",
                valor = monto,
                hint = "Ej: 15000",
                teclado = KeyboardType.Number,
                icono = { Icon(Icons.Default.AttachMoney, null, tint = NavyPrimary) },
                onCambia = { monto = it; guardar("sol_monto", it) }
            )

            CampoSolicitud(
                label = "Plazo (meses)",
                valor = plazo,
                hint = "Ej: 24",
                teclado = KeyboardType.Number,
                icono = { Icon(Icons.Default.CalendarMonth, null, tint = NavyPrimary) },
                onCambia = { plazo = it; guardar("sol_plazo", it) }
            )

            CampoSolicitud(
                label = "Tipo de crédito",
                valor = tipo,
                hint = "Personal / Hipotecario / Vehicular",
                teclado = KeyboardType.Text,
                icono = { Icon(Icons.Default.AccountBalance, null, tint = NavyPrimary) },
                onCambia = { tipo = it; guardar("sol_tipo", it) }
            )

            CampoSolicitud(
                label = "DNI del solicitante",
                valor = dni,
                hint = "12345678",
                teclado = KeyboardType.Number,
                icono = { Icon(Icons.Default.Badge, null, tint = NavyPrimary) },
                onCambia = {
                    if (it.length <= 8) {
                        dni = it
                        guardar("sol_dni", it)
                    }
                }
            )

            if (mensajeError.isNotEmpty()) {
                Text(
                    text = mensajeError,
                    color = RedNegative,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (monto.isEmpty() || plazo.isEmpty() || tipo.isEmpty() || dni.isEmpty()) {
                        mensajeError = "Completa todos los campos para enviar."
                    } else {
                        mensajeError = ""
                        mostrarDialogoEnvio = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Enviar Solicitud",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = {
                    textoHistorial = LogManager.obtenerHistorial(contexto)
                    mostrarDialogoHistorial = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Ver Historial de Solicitudes",
                    color = NavyPrimary
                )
            }

            TextButton(
                onClick = { mostrarDialogoBorrador = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Limpiar borrador",
                    color = GrayMedium
                )
            }
        }
    }

    // Diálogos
    if (mostrarDialogoEnvio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEnvio = false },
            title = { Text("Solicitud enviada", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tu solicitud ha sido registrada.")
                    Spacer(Modifier.height(4.dp))
                    Text("Monto: S/ $monto")
                    Text("Plazo: $plazo meses")
                    Text("Tipo: $tipo")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val detalle = "Monto: S/ $monto | Plazo: $plazo meses | Tipo: $tipo | DNI: $dni"
                        LogManager.registrar(contexto, detalle)
                        limpiarBorrador()
                        mostrarDialogoEnvio = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (mostrarDialogoHistorial) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoHistorial = false },
            title = { Text("Historial de Solicitudes", fontWeight = FontWeight.Bold) },
            text = { Text(text = textoHistorial, fontSize = 12.sp, color = NavyDark) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoHistorial = false }) {
                    Text("Cerrar", color = NavyPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoHistorial = false
                    mostrarDialogoLimpiarHis = true
                }) {
                    Text("Limpiar historial", color = RedNegative)
                }
            }
        )
    }

    if (mostrarDialogoLimpiarHis) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLimpiarHis = false },
            title = { Text("Limpiar historial", fontWeight = FontWeight.Bold) },
            text = { Text("¿Eliminar todo el historial de solicitudes?") },
            confirmButton = {
                Button(
                    onClick = {
                        LogManager.registrar(contexto, "--- Historial limpiado por el usuario ---")
                        LogManager.limpiarHistorial(contexto)
                        mostrarDialogoLimpiarHis = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedNegative)
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoLimpiarHis = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoBorrador) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrador = false },
            title = { Text("Limpiar borrador", fontWeight = FontWeight.Bold) },
            text = { Text("¿Borrar los datos del formulario?") },
            confirmButton = {
                Button(
                    onClick = {
                        limpiarBorrador()
                        mostrarDialogoBorrador = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedNegative)
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoBorrador = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CampoSolicitud(
    label: String,
    valor: String,
    hint: String,
    teclado: KeyboardType,
    icono: @Composable () -> Unit,
    onCambia: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambia,
        label = { Text(label) },
        placeholder = { Text(hint, color = GrayMedium) },
        leadingIcon = icono,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}