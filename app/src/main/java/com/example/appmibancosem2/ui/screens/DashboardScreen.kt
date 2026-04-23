package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.navigation.Screen
import com.example.appmibancosem2.ui.theme.GoldAccent
import com.example.appmibancosem2.ui.theme.GreenPositive
import com.example.appmibancosem2.ui.theme.NavyDark
import com.example.appmibancosem2.ui.theme.NavyLight
import com.example.appmibancosem2.ui.theme.NavyPrimary

@Composable
fun DashboardScreen(
    onNavigateTo: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(topBar = { MiBancoTopBar(titulo = "Mi Banco") }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(34.dp)
                )
                Spacer(Modifier.padding(4.dp))
                Column {
                    Text(
                        text = "Hola, ${DemoData.cuenta.titular.split(" ").first()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark
                    )
                    Text("Tus productos y movimientos", color = Color.Gray, fontSize = 12.sp)
                }
            }

            DemoData.cuentas.forEach { cuenta ->
                TarjetaCuenta(cuenta = cuenta)
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Accesos rápidos",
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDark,
                        fontSize = 15.sp
                    )

                    // Fila 1: Pagar, Préstamo, Ahorro
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BotonAccesoRapido(Icons.Default.Payment, "Pagar", GreenPositive) { onNavigateTo(Screen.Pagos) }
                        BotonAccesoRapido(Icons.Default.AccountBalance, "Préstamo", GoldAccent) { onNavigateTo(Screen.Prestamos) }
                        BotonAccesoRapido(Icons.Default.Savings, "Ahorro", NavyLight) { onNavigateTo(Screen.Ahorro) }
                    }

                    // Fila 2: Crédito e Historial
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BotonAccesoRapido(
                            icono = Icons.Default.RequestPage,
                            etiqueta = "Crédito",
                            color = GreenPositive,
                            onClick = { onNavigateTo(Screen.SolicitudCredito) }
                        )
                        BotonAccesoRapido(
                            icono = Icons.Default.Receipt,
                            etiqueta = "Historial",
                            color = NavyPrimary,
                            onClick = { onNavigateTo(Screen.Transacciones) }
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Últimos movimientos",
                            fontWeight = FontWeight.SemiBold,
                            color = NavyDark
                        )
                        TextButton(onClick = { onNavigateTo(Screen.Transacciones) }) {
                            Text("Ver todos", color = GoldAccent, fontSize = 12.sp)
                        }
                    }
                    DemoData.transacciones.take(4).forEach { tx ->
                        FilaTransaccion(transaccion = tx)
                    }
                }
            }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.Icon(Icons.Default.Logout, null)
                Spacer(Modifier.padding(4.dp))
                Text("Cerrar sesión")
            }
        }
    }
}