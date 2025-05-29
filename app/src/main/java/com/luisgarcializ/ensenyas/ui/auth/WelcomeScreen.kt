package com.luisgarcializ.ensenyas.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.luisgarcializ.ensenyas.R
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientEnd
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientStart
import com.luisgarcializ.ensenyas.ui.theme.VeryDarkBlue
import com.luisgarcializ.ensenyas.ui.theme.VeryLightBlueButton
import com.luisgarcializ.ensenyas.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen(
    navController: NavController,
    auth: FirebaseAuth,
    authViewModel: AuthViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            )
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(color = Color.Transparent)
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoreal),
                contentDescription = "Logo enSeñas",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "¡Bienvenido!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 36.sp,
                    color = VeryDarkBlue
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Aprende la Lengua de Signos Española hoy mismo.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    color = VeryDarkBlue
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navController.navigate("sign_in")
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp), // ¡CAMBIO AQUÍ!
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Iniciar Sesión", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("sign_up")
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp), // ¡CAMBIO AQUÍ!
            colors = ButtonDefaults.buttonColors(
                containerColor = VeryLightBlueButton,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Crear Cuenta", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.weight(0.2f))
    }
}