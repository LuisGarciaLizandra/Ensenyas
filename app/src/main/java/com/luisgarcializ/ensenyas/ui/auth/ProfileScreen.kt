package com.luisgarcializ.ensenyas.ui.auth

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.luisgarcializ.ensenyas.R
import com.luisgarcializ.ensenyas.model.Leccion
import com.luisgarcializ.ensenyas.model.ProgresoLeccion
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientEnd
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientStart
import com.luisgarcializ.ensenyas.ui.theme.VeryDarkBlue
import com.luisgarcializ.ensenyas.ui.theme.OffWhite
import com.luisgarcializ.ensenyas.viewmodel.UserProfileViewModel
import com.luisgarcializ.ensenyas.ui.theme.VeryLightBlueButton
import com.luisgarcializ.ensenyas.ui.common.LoadingView
import com.luisgarcializ.ensenyas.ui.common.ErrorView


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    auth: FirebaseAuth,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val currentUserData by userProfileViewModel.currentUserData.observeAsState()
    val userProgress by userProfileViewModel.userProgress.observeAsState(emptyMap())
    val allLessons by userProfileViewModel.allLessons.observeAsState(emptyList())
    val isLoading by userProfileViewModel.isLoading.observeAsState(true)
    val errorMessage by userProfileViewModel.errorMessage.observeAsState()

    val scrollState = rememberScrollState()

    val avatarDrawableNames = remember {
        listOf(
            "avatar_1", "avatar_2", "avatar_3", "avatar_4", "avatar_5", "avatar_6"
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            ),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.topbar),
                        contentDescription = "Logo de la aplicación",
                        modifier = Modifier.fillMaxWidth(0.85f),
                        contentScale = ContentScale.Fit
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(36.dp),
                            tint = VeryDarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = VeryDarkBlue
                )
            )
        }
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
                    .padding(horizontal = 16.dp)
                    .background(color = Color.Transparent)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    isLoading -> {
                        LoadingView()
                    }

                    errorMessage != null -> {
                        ErrorView(errorMessage)
                    }

                    currentUserData == null -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No se pudo cargar la información del usuario.",
                                style = MaterialTheme.typography.bodyLarge.copy(color = VeryDarkBlue),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                navController.navigate("welcome") {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }
                            }) {
                                Text("Volver al inicio")
                            }
                        }
                    }

                    else -> {
                        val user = currentUserData!!
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(width = 2.dp, color = VeryDarkBlue)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val isLocalAvatar = avatarDrawableNames.contains(user.avatarUrl)
                                if (isLocalAvatar) {
                                    val drawableId = LocalContext.current.resources.getIdentifier(
                                        user.avatarUrl,
                                        "drawable",
                                        LocalContext.current.packageName
                                    )
                                    if (drawableId != 0) {
                                        Image(
                                            painter = painterResource(id = drawableId),
                                            contentDescription = "Avatar de usuario",
                                            modifier = Modifier.size(150.dp)
                                                .clip(MaterialTheme.shapes.extraLarge),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Avatar por defecto",
                                            modifier = Modifier.size(150.dp),
                                            tint = VeryDarkBlue
                                        )
                                    }
                                } else {
                                    val imageUrl = user.avatarUrl.replace(
                                        "gs://ensenyas.firebasestorage.app/",
                                        "https://firebasestorage.googleapis.com/v0/b/ensenyas.firebasestorage.app/o/"
                                    ).replace(
                                        "gs://ensenyas.appspot.com/",
                                        "https://firebasestorage.googleapis.com/v0/b/ensenyas.appspot.com/o/"
                                    )
                                    val finalImageUrl =
                                        if (imageUrl.contains("firebasestorage.googleapis.com") && !imageUrl.contains(
                                                "?alt=media"
                                            )
                                        ) {
                                            "$imageUrl?alt=media"
                                        } else {
                                            imageUrl
                                        }

                                    AsyncImage(
                                        model = finalImageUrl,
                                        contentDescription = "Avatar de usuario",
                                        modifier = Modifier.size(150.dp)
                                            .clip(MaterialTheme.shapes.extraLarge),
                                        contentScale = ContentScale.Crop,
                                        error = rememberVectorPainter(Icons.Default.Person),
                                        placeholder = rememberVectorPainter(Icons.Default.Person)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = user.username,
                                    style = MaterialTheme.typography.headlineMedium.copy(color = VeryDarkBlue)
                                )
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = VeryDarkBlue),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "Fecha de Nacimiento: ${user.fechaNacimiento}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = VeryDarkBlue),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // SE ELIMINA EL BOTÓN Y EL TEXTO "Modo Minimalista"
                                // Row(
                                //     modifier = Modifier.fillMaxWidth(),
                                //     verticalAlignment = Alignment.CenterVertically,
                                //     horizontalArrangement = Arrangement.SpaceBetween
                                // ) {
                                //     Text(
                                //         text = "Modo Minimalista",
                                //         style = MaterialTheme.typography.bodyLarge.copy(color = VeryDarkBlue)
                                //     )
                                //     Switch(
                                //         checked = user.configuracion.modoMinimalista,
                                //         onCheckedChange = { isChecked ->
                                //             userProfileViewModel.updateMinimalistMode(isChecked)
                                //         }
                                //     )
                                // }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { navController.navigate("avatar_selection_screen?fromSignup=false") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VeryLightBlueButton,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Cambiar Avatar", style = MaterialTheme.typography.titleMedium)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Progreso de Lecciones",
                            style = MaterialTheme.typography.headlineSmall.copy(color = VeryDarkBlue),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (allLessons.isEmpty()) {
                            Text(
                                text = "No hay lecciones disponibles.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = VeryDarkBlue)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .height((allLessons.size * (100 + 12)).dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(allLessons) { leccion ->
                                    val progreso = userProgress[leccion.idLeccion]
                                    val completada = progreso?.completada ?: false
                                    val temasCompletadosCount =
                                        progreso?.temasCompletados?.count { it.value } ?: 0
                                    val totalTemas = leccion.temas.size
                                    val porcentaje =
                                        if (totalTemas > 0) (temasCompletadosCount * 100) / totalTemas else 0

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (completada) Color.White else OffWhite
                                        ),
                                        border = BorderStroke(width = 2.dp, color = VeryDarkBlue)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp)
                                                .padding(10.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = leccion.titulo,
                                                style = MaterialTheme.typography.titleLarge,
                                                color = VeryDarkBlue,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = if (completada) "Completada (100%)" else "En progreso ($porcentaje%)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = VeryDarkBlue,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                userProfileViewModel.signOut()
                                navController.navigate("welcome") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onError)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}