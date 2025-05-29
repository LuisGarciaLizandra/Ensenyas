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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.luisgarcializ.ensenyas.viewmodel.AuthViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    auth: FirebaseAuth,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
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
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium.copy(color = VeryDarkBlue),
                    modifier = Modifier.padding(bottom = 24.dp)
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
                    label = { Text("Password") },
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(text = "Remember me", style = MaterialTheme.typography.bodySmall.copy(color = VeryDarkBlue))
                    }
                    Text(
                        text = "Forgot Password?",
                        modifier = Modifier.clickable { },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        authViewModel.signInUser(email, password) { success, errorMessage ->
                            if (success) {
                                Toast.makeText(context, "Inicio de sesión exitoso!", Toast.LENGTH_LONG).show()
                                navController.navigate("lessons_list") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Error al iniciar sesión: ${errorMessage ?: "Desconocido"}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "Log In", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), thickness = 1.dp)
                    Text(
                        text = " OR ",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), thickness = 1.dp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Login con Google",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Continue with Google", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Login con Facebook",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Continue with Facebook", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                            append("Sign Up")
                        }
                    },
                    modifier = Modifier.clickable { navController.navigate("sign_up") },
                    style = MaterialTheme.typography.bodyMedium,
                    color = VeryDarkBlue
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}