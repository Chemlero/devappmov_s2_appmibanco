package com.example.appmibancosem2.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.ui.theme.GoldAccent
import com.example.appmibancosem2.ui.theme.NavyDark
import com.example.appmibancosem2.ui.theme.NavyPrimary

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val contexto = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var recordarSesion by remember { mutableStateOf(false) }

    // Cargar el correo y la contraseña guardados al iniciar la pantalla
    LaunchedEffect(Unit) {
        val prefs = contexto.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        email = prefs.getString("email", "") ?: ""
        password = prefs.getString("password", "") ?: ""
        recordarSesion = prefs.getBoolean("recordar_sesion", false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyPrimary))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Mi Banco", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = NavyPrimary)
                Text("Portal Financiero", fontSize = 14.sp, color = GoldAccent)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; error = "" },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = NavyPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; error = "" },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = NavyPrimary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = recordarSesion,
                        onCheckedChange = { recordarSesion = it }
                    )
                    Text("Recordar sesión", color = NavyDark, fontSize = 14.sp)
                }

                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        error = when {
                            email.isBlank() || password.isBlank() -> "Completa todos los campos"
                            !email.contains("@") -> "Ingresa un correo válido"
                            password.length < 4 -> "La contraseña es demasiado corta"
                            else -> {
                                // Guardar el correo, la contraseña y el estado de "recordar sesión" si está activado
                                if (recordarSesion) {
                                    contexto.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .putString("email", email)
                                        .putString("password", password)
                                        .putBoolean("recordar_sesion", true)
                                        .apply()
                                } else {
                                    contexto.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .remove("email")
                                        .remove("password")
                                        .putBoolean("recordar_sesion", false)
                                        .apply()
                                }
                                onLoginSuccess()
                                ""
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = {}) {
                    Text("¿Olvidaste tu contraseña?", color = GoldAccent, fontSize = 13.sp)
                }
            }
        }
    }
}