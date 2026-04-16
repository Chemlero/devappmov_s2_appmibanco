package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.Cuenta
import com.example.appmibancosem2.data.model.Transaccion
import com.example.appmibancosem2.data.model.formatoMoneda
import com.example.appmibancosem2.ui.theme.GoldAccent
import com.example.appmibancosem2.ui.theme.GoldLight
import com.example.appmibancosem2.ui.theme.GrayMedium
import com.example.appmibancosem2.ui.theme.GreenPositive
import com.example.appmibancosem2.ui.theme.NavyDark
import com.example.appmibancosem2.ui.theme.NavyLight
import com.example.appmibancosem2.ui.theme.NavyPrimary
import com.example.appmibancosem2.ui.theme.RedNegative
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiBancoTopBar(
    titulo: String,
    mostrarBack: Boolean = false,
    onBack: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = titulo,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (mostrarBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NavyPrimary)
    )
}

@Composable
fun TarjetaCuenta(cuenta: Cuenta, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(NavyPrimary, NavyLight)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = cuenta.tipo,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White)
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(cuenta.numeroEnmascarado(), color = Color.White.copy(alpha = 0.8f), letterSpacing = 2.sp)
                    Text("Saldo disponible", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(
                        text = cuenta.saldo.formatoMoneda(),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(cuenta.titular, color = GoldLight, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun BotonAccesoRapido(
    icono: ImageVector,
    etiqueta: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = color.copy(alpha = 0.14f))
        ) {
            Icon(icono, contentDescription = etiqueta, tint = color, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(etiqueta, fontSize = 12.sp, color = NavyDark, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FilaTransaccion(transaccion: Transaccion) {
    val esDebito = transaccion.esDebito()
    val color = if (esDebito) RedNegative else GreenPositive
    val icono = if (esDebito) Icons.Default.NorthEast else Icons.Default.SouthWest

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = color)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaccion.descripcion, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = NavyDark)
            Text(transaccion.fecha, fontSize = 12.sp, color = GrayMedium)
        }
        Text(transaccion.montoConSigno(), color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
    HorizontalDivider(color = GrayMedium.copy(alpha = 0.3f))
}