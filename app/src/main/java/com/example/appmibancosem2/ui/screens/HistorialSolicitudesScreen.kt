package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.local.SolicitudDatabase
import com.example.appmibancosem2.data.model.SolicitudCredito
import com.example.appmibancosem2.ui.theme.*
import java.text.DecimalFormat

@Composable
fun HistorialSolicitudesScreen(onBack: () -> Unit) {
    val contexto = LocalContext.current
    val db = remember { SolicitudDatabase(contexto) }
    var solicitudes by remember { mutableStateOf(emptyList<SolicitudCredito>()) }
    var pendientes by remember { mutableStateOf(0) }

    // Función para recargar la lista después de cualquier cambio
    fun recargar() {
        solicitudes = db.obtenerTodas()
        pendientes = db.contarPendientes()
    }

    LaunchedEffect(Unit) { recargar() }

    Scaffold(
        topBar = {
            MiBancoTopBar(
                titulo = "Historial de Solicitudes",
                mostrarBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Banner resumen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${solicitudes.size} solicitudes",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "guardadas localmente en SQLite",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    if (pendientes > 0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$pendientes pendientes",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Lista vacía
            if (solicitudes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = GrayMedium,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay solicitudes registradas",
                            color = GrayMedium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Envía una solicitud de crédito para verla aquí",
                            color = GrayMedium,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Lista de solicitudes
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = solicitudes,
                        key = { it.id }
                    ) { sol ->
                        TarjetaSolicitud(
                            solicitud = sol,
                            onMarcarEnviada = {
                                db.actualizarEstado(sol.id, "enviada")
                                recargar()
                            },
                            onEliminar = {
                                db.eliminar(sol.id)
                                recargar()
                            }
                        )
                    }
                }
            }
        }
    }
}

// Componente: Tarjeta con acciones CRUD
@Composable
private fun TarjetaSolicitud(
    solicitud: SolicitudCredito,
    onMarcarEnviada: () -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    val colorEstado = when (solicitud.estado) {
        "enviada" -> GreenPositive
        "pendiente" -> GoldAccent
        "rechazada" -> RedNegative
        else -> GrayMedium
    }
    val formatoMonto = DecimalFormat("S/ #,##0.00")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: datos + badge estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Barra lateral de color
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(52.dp)
                ) {
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        thickness = 4.dp,
                        color = colorEstado
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${formatoMonto.format(solicitud.monto)} — ${solicitud.plazoMeses} meses",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = solicitud.tipo,
                        color = GrayMedium,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "DNI: ${solicitud.dni} · ${solicitud.fechaLocal}",
                        color = GrayMedium,
                        fontSize = 11.sp
                    )
                }
                // Badge de estado
                Surface(
                    color = colorEstado.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = solicitud.estado,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = colorEstado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Fila inferior: botones de acción
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Marcar enviada (solo si está pendiente)
                if (solicitud.estado == "pendiente") {
                    OutlinedButton(
                        onClick = onMarcarEnviada,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPositive)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Marcar enviada", fontSize = 12.sp)
                    }
                }

                // Botón Eliminar
                OutlinedButton(
                    onClick = { mostrarConfirmacion = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedNegative)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Eliminar", fontSize = 12.sp)
                }
            }
        }
    }

    // Diálogo de confirmación antes de eliminar
    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = {
                Text(text = "Eliminar solicitud", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "¿Eliminar la solicitud de ${formatoMonto.format(solicitud.monto)}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacion = false
                        onEliminar()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedNegative)
                ) {
                    Text(text = "Sí, eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarConfirmacion = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}