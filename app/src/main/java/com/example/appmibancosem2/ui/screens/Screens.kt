package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.data.model.SimuladorPrestamo
import com.example.appmibancosem2.data.model.Transaccion
import com.example.appmibancosem2.data.model.formatoMoneda
import com.example.appmibancosem2.ui.theme.GoldAccent
import com.example.appmibancosem2.ui.theme.GoldLight
import com.example.appmibancosem2.ui.theme.GrayMedium
import com.example.appmibancosem2.ui.theme.GreenPositive
import com.example.appmibancosem2.ui.theme.NavyDark
import com.example.appmibancosem2.ui.theme.NavyPrimary
import com.example.appmibancosem2.ui.theme.RedNegative
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransaccionesScreen(onBack: () -> Unit) {
    var filtro by remember { mutableStateOf("todos") }
    val movimientosFiltrados = remember(filtro) {
        when (filtro) {
            "debito" -> DemoData.transacciones.filter { it.esDebito() }
            "credito" -> DemoData.transacciones.filter { !it.esDebito() }
            else -> DemoData.transacciones
        }
    }
    val totalDebitos = DemoData.transacciones.count { it.esDebito() }
    val totalCreditos = DemoData.transacciones.count { !it.esDebito() }

    Scaffold(topBar = { MiBancoTopBar(titulo = "Movimientos", mostrarBack = true, onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TarjetaCuenta(cuenta = DemoData.cuenta)
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Historial de movimientos", fontWeight = FontWeight.Bold, color = NavyDark)
                        Text("$totalDebitos débitos | $totalCreditos créditos", color = GrayMedium, fontSize = 13.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("todos" to "Todos", "debito" to "Débitos", "credito" to "Créditos").forEach { (clave, etiqueta) ->
                                FilterChip(
                                    selected = filtro == clave,
                                    onClick = { filtro = clave },
                                    label = { Text(etiqueta) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (clave == "debito") RedNegative else if (clave == "credito") GreenPositive else NavyPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
            items(movimientosFiltrados) { tx ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
                        FilaTransaccion(transaccion = tx)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(onBack: () -> Unit) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var servicioSeleccionado by remember { mutableStateOf("") }
    var expandido by remember { mutableStateOf(false) }
    var numeroContrato by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var mostrarErrores by remember { mutableStateOf(false) }
    var mostrarModal by remember { mutableStateOf(false) }
    var pagoListo by remember { mutableStateOf(false) }

    val servicioValido = servicioSeleccionado.isNotBlank()
    val contratoValido = numeroContrato.length >= 6
    val montoValido = monto.toDoubleOrNull()?.let { it > 0 } ?: false
    val formularioValido = servicioValido && contratoValido && montoValido
    val montoDouble = monto.toDoubleOrNull() ?: 0.0

    LaunchedEffect(formularioValido) {
        pagoListo = formularioValido
    }

    Scaffold(
        topBar = { MiBancoTopBar(titulo = "Pago de Servicios", mostrarBack = true, onBack = onBack) },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Completa los datos del servicio", fontWeight = FontWeight.SemiBold, color = NavyDark)

            ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = !expandido }) {
                OutlinedTextField(
                    value = servicioSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    isError = mostrarErrores && !servicioValido,
                    label = { Text("Servicio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                    DemoData.servicios.forEach { servicio ->
                        DropdownMenuItem(
                            text = { Text(servicio.nombre) },
                            onClick = {
                                servicioSeleccionado = servicio.nombre
                                expandido = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = numeroContrato,
                onValueChange = { numeroContrato = it.filter(Char::isDigit).take(12) },
                label = { Text("Número de contrato") },
                leadingIcon = { Icon(Icons.Default.Article, null, tint = NavyPrimary) },
                isError = mostrarErrores && !contratoValido,
                supportingText = {
                    if (mostrarErrores && !contratoValido) {
                        Text("Debe tener al menos 6 dígitos")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = monto,
                onValueChange = { nuevo ->
                    monto = nuevo.filter { it.isDigit() || it == '.' }.replace("..", ".")
                },
                label = { Text("Monto a pagar (S/)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null, tint = NavyPrimary) },
                isError = mostrarErrores && !montoValido,
                supportingText = {
                    if (mostrarErrores && !montoValido) {
                        Text("Ingresa un monto positivo")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (formularioValido) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBFF)), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Resumen del pago", fontWeight = FontWeight.SemiBold, color = NavyDark)
                        FilaResumen("Servicio", servicioSeleccionado)
                        FilaResumen("Contrato", numeroContrato)
                        FilaResumen("Monto", montoDouble.formatoMoneda())
                    }
                }
                Surface(color = GreenPositive.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = GreenPositive)
                        Spacer(Modifier.width(8.dp))
                        Text("✓ Listo para confirmar", color = GreenPositive, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Button(
                onClick = {
                    mostrarErrores = true
                    if (formularioValido) {
                        mostrarModal = true
                    }
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (formularioValido) NavyPrimary else Color.Gray
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Pagar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarModal) {
        AlertDialog(
            onDismissRequest = { mostrarModal = false },
            title = { Text("Confirmar pago", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Servicio: $servicioSeleccionado")
                    Text("Contrato: $numeroContrato")
                    Text("Monto: ${montoDouble.formatoMoneda()}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarModal = false
                        scope.launch { snackBarHostState.showSnackbar("Pago registrado correctamente") }
                        servicioSeleccionado = ""
                        numeroContrato = ""
                        monto = ""
                        mostrarErrores = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrestamosScreen(onBack: () -> Unit) {
    var monto by remember { mutableStateOf(5000f) }
    var plazoIndex by remember { mutableStateOf(1) }
    var tasaIndex by remember { mutableStateOf(1) }

    val plazos = listOf(6, 12, 24, 36)
    val tasas = listOf(18.0, 24.0, 30.0)

    val simulador by remember(monto, plazoIndex, tasaIndex) {
        derivedStateOf {
            SimuladorPrestamo(
                monto = monto.toDouble(),
                tasaAnual = tasas[tasaIndex],
                cuotas = plazos[plazoIndex]
            )
        }
    }

    val cuota by remember(simulador) { derivedStateOf { simulador.calcularCuota() } }
    val totalPagar by remember(simulador) { derivedStateOf { simulador.totalAPagar() } }
    val intereses by remember(simulador) { derivedStateOf { simulador.interesesTotales() } }
    val detalle = remember(simulador) { simulador.primerasCuotas() }

    Scaffold(topBar = { MiBancoTopBar(titulo = "Simulador de Préstamos", mostrarBack = true, onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = NavyPrimary), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cuota mensual", color = GoldLight, fontSize = 13.sp)
                    Text(cuota.formatoMoneda(), fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Monto: ${monto.toDouble().formatoMoneda()} | ${plazos[plazoIndex]} meses | ${tasas[tasaIndex].toInt()}%", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Monto del préstamo", fontWeight = FontWeight.Medium, color = NavyDark)
                    Text(monto.toDouble().formatoMoneda(), fontWeight = FontWeight.Bold, color = NavyPrimary)
                }
                androidx.compose.material3.Slider(
                    value = monto,
                    onValueChange = { monto = it },
                    valueRange = 1000f..50000f,
                    steps = 48,
                    colors = androidx.compose.material3.SliderDefaults.colors(activeTrackColor = NavyPrimary, thumbColor = NavyPrimary)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("S/ 1,000", fontSize = 11.sp, color = GrayMedium)
                    Text("S/ 50,000", fontSize = 11.sp, color = GrayMedium)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Plazo", fontWeight = FontWeight.Medium, color = NavyDark)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    plazos.forEachIndexed { index, plazo ->
                        FilterChip(
                            selected = plazoIndex == index,
                            onClick = { plazoIndex = index },
                            label = { Text("$plazo meses") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tasa anual", fontWeight = FontWeight.Medium, color = NavyDark)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tasas.forEachIndexed { index, tasa ->
                        FilterChip(
                            selected = tasaIndex == index,
                            onClick = { tasaIndex = index },
                            label = { Text("${tasa.toInt()}%") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F7FF)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Resumen", fontWeight = FontWeight.SemiBold, color = NavyDark)
                    FilaResumen("Cuota mensual", cuota.formatoMoneda())
                    FilaResumen("Total a pagar", totalPagar.formatoMoneda())
                    FilaResumen("Intereses totales", intereses.formatoMoneda())
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Primeras 6 cuotas", fontWeight = FontWeight.SemiBold, color = NavyDark)
                    detalle.forEach { cuotaDetalle ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Mes ${cuotaDetalle.mes}", fontWeight = FontWeight.Bold, color = NavyPrimary)
                            FilaResumen("Cuota", cuotaDetalle.cuota.formatoMoneda())
                            FilaResumen("Capital amortizado", cuotaDetalle.capital.formatoMoneda())
                            FilaResumen("Interés del mes", cuotaDetalle.interes.formatoMoneda())
                            HorizontalDivider()
                        }
                    }
                }
            }

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AccountBalanceWallet, null)
                Spacer(Modifier.width(8.dp))
                Text("Solicitar préstamo", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AhorroScreen(onBack: () -> Unit) {
    val ahorro = DemoData.cuentaAhorro
    val pct = ahorro.porcentaje()
    val mesesMeta = ahorro.calcularMesesParaMeta()
    val proyeccion = ahorro.proyeccionSeisMeses()

    Scaffold(topBar = { MiBancoTopBar(titulo = "Cuenta de Ahorro", mostrarBack = true, onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = NavyPrimary), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(ahorro.nombre, color = GoldLight, fontWeight = FontWeight.SemiBold)
                    FilaResumenDark("Saldo actual", ahorro.saldo.formatoMoneda())
                    FilaResumenDark("Meta", ahorro.meta.formatoMoneda())
                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier.fillMaxWidth().height(14.dp),
                        color = GreenPositive,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )
                    Text("${(pct * 100).toInt()}% completado", color = Color.White, fontSize = 12.sp)
                }
            }

            Surface(color = Color(0xFFF6FAF6), shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(ahorro.mensajeMotivacional(), fontWeight = FontWeight.Bold, color = GreenPositive)
                    Text("Te faltan ${ahorro.faltaParaMeta().formatoMoneda()} para tu meta", color = NavyDark)
                    Text("Alcanzarás tu meta en $mesesMeta meses aprox. (${ahorro.fechaMetaAproximada()})", color = NavyDark)
                    AssistChip(
                        onClick = {},
                        label = { Text("Depósito mensual ${ahorro.depositoMensual.formatoMoneda()}") },
                        leadingIcon = { Icon(Icons.Default.Savings, null) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = GreenPositive.copy(alpha = 0.12f))
                    )
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Proyección próximos 6 meses", fontWeight = FontWeight.SemiBold, color = NavyDark)
                    proyeccion.forEach { fila ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Mes ${fila.numeroMes}", fontWeight = FontWeight.Bold, color = NavyPrimary)
                            FilaResumen("Depósito", fila.deposito.formatoMoneda())
                            FilaResumen("Interés", fila.interes.formatoMoneda())
                            FilaResumen("Saldo proyectado", fila.saldoProyectado.formatoMoneda())
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaResumen(label: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = GrayMedium, fontSize = 13.sp)
        Text(valor, fontWeight = FontWeight.Medium, color = NavyDark, fontSize = 13.sp)
    }
}

@Composable
private fun FilaResumenDark(label: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
        Text(valor, fontWeight = FontWeight.Medium, color = Color.White, fontSize = 13.sp)
    }
}
