package com.luisgarcializ.ensenyas.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // Importar para la cuadrícula
import androidx.compose.foundation.lazy.grid.items // Para items en LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.luisgarcializ.ensenyas.R // Necesario para R.drawable
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientEnd
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientStart
import com.luisgarcializ.ensenyas.ui.theme.VeryDarkBlue
import com.luisgarcializ.ensenyas.viewmodel.UserProfileViewModel // Necesario para updateUserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarSelectionScreen(
    navController: NavController,
    auth: FirebaseAuth,
    userProfileViewModel: UserProfileViewModel = viewModel(),
    fromSignup: Boolean = false // Parámetro para saber si venimos del registro
) {
    val currentUserData by userProfileViewModel.currentUserData.observeAsState()
    val context = LocalContext.current

    val avatarDrawables = remember {
        listOf(
            R.drawable.avatar_1,
            R.drawable.avatar_2,
            R.drawable.avatar_3,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6
        )
    }
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
                        onClick = {
                            if (fromSignup) {
                                // Si viene de registro, lleva a lessons_list
                                navController.navigate("lessons_list") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            } else {
                                // Si viene de perfil, simplemente vuelve atrás
                                navController.popBackStack()
                            }
                        },
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
                    .background(color = Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Elige tu Avatar",
                    style = MaterialTheme.typography.headlineMedium.copy(color = VeryDarkBlue),
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // Cuadrícula de 3 columnas
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(avatarDrawables.size) { index ->
                        val avatarResId = avatarDrawables[index]
                        val avatarName = avatarDrawableNames[index]
                        val isSelected = currentUserData?.avatarUrl == avatarName // Comprobar si está seleccionado

                        Card(
                            modifier = Modifier
                                .size(100.dp) // Tamaño de cada avatar en la cuadrícula
                                .clickable {
                                    userProfileViewModel.updateUserAvatar(avatarName) // Guardar el nombre del drawable
                                    // Después de seleccionar, si viene del registro, navegar a LessonsList
                                    if (fromSignup) {
                                        navController.navigate("lessons_list") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    } else {
                                        // Si viene de perfil, solo volver atrás
                                        navController.popBackStack()
                                    }
                                },
                            shape = MaterialTheme.shapes.extraLarge, // Hacerlos circulares o muy redondeados
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = if (isSelected) BorderStroke(4.dp, MaterialTheme.colorScheme.primary) else BorderStroke(2.dp, Color.Gray)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = avatarResId),
                                    contentDescription = "Avatar ${index + 1}",
                                    modifier = Modifier.fillMaxSize().padding(4.dp).clip(MaterialTheme.shapes.extraLarge),
                                    contentScale = ContentScale.Crop
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Seleccionado",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(4.dp)
                                            .size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}