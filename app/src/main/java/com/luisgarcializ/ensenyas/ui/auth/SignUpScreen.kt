package com.luisgarcializ.ensenyas.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.luisgarcializ.ensenyas.R
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientEnd
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientStart
import com.luisgarcializ.ensenyas.ui.theme.VeryDarkBlue
import com.luisgarcializ.ensenyas.ui.theme.OffWhite
import com.luisgarcializ.ensenyas.ui.theme.VeryLightBlueButton
import androidx.compose.material3.CheckboxDefaults
import com.luisgarcializ.ensenyas.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    auth: FirebaseAuth,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.weight(0.5f))

                Image(
                    painter = painterResource(id = R.drawable.logoletras),
                    contentDescription = "Logo enSeñas",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineMedium.copy(color = VeryDarkBlue),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = OffWhite,
                        unfocusedContainerColor = OffWhite,
                        disabledContainerColor = OffWhite,
                        errorContainerColor = OffWhite
                    )
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = OffWhite,
                        unfocusedContainerColor = OffWhite,
                        disabledContainerColor = OffWhite,
                        errorContainerColor = OffWhite
                    )
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = OffWhite,
                        unfocusedContainerColor = OffWhite,
                        disabledContainerColor = OffWhite,
                        errorContainerColor = OffWhite
                    )
                )
                TextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = OffWhite,
                        unfocusedContainerColor = OffWhite,
                        disabledContainerColor = OffWhite,
                        errorContainerColor = OffWhite
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Acepto los ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                append("términos y condiciones")
                            }
                            append(" y la ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                append("política de privacidad")
                            }
                        },
                        modifier = Modifier.clickable { /* TODO: Navegar a los documentos de políticas */ },
                        style = MaterialTheme.typography.bodySmall,
                        color = VeryDarkBlue
                    )
                }

                Button(
                    onClick = {
                        authViewModel.registerUser(email, password, username, birthDate) { success, errorMessage ->
                            if (success) {
                                Toast.makeText(context, "Cuenta creada exitosamente!", Toast.LENGTH_LONG).show()
                                // ¡CAMBIO AQUÍ! Navega a la pantalla de selección de avatar
                                navController.navigate("avatar_selection_screen?fromSignup=true") {
                                    popUpTo("welcome") { inclusive = true } // Limpia la pila de navegación
                                }
                            } else {
                                Toast.makeText(context, "Error: ${errorMessage ?: "Desconocido"}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = agreedToTerms
                ) {
                    Text(text = "Registrarse", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = buildAnnotatedString {
                        append("¿Ya tienes una cuenta? ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                            append("Inicia Sesión")
                        }
                    },
                    modifier = Modifier.clickable { navController.navigate("sign_in") },
                    style = MaterialTheme.typography.bodyMedium,
                    color = VeryDarkBlue
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}