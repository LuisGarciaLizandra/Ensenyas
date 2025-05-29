package com.luisgarcializ.ensenyas.ui.lessons



import android.widget.Toast

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.Image

import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.*

import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.getValue

import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.alpha

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

import com.luisgarcializ.ensenyas.R

import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientEnd

import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientStart

import com.luisgarcializ.ensenyas.ui.theme.VeryDarkBlue

import com.luisgarcializ.ensenyas.viewmodel.LessonViewModel

import com.luisgarcializ.ensenyas.viewmodel.UserProfileViewModel



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun LessonDetailScreen(

    navController: NavController,

    auth: FirebaseAuth,

    lessonViewModel: LessonViewModel = viewModel(),

    leccionId: String?,

    userProfileViewModel: UserProfileViewModel = viewModel()

) {

    val selectedLeccionState = lessonViewModel.selectedLeccion.observeAsState()

    val currentUserData by userProfileViewModel.currentUserData.observeAsState()

    val context = LocalContext.current



    LaunchedEffect(key1 = leccionId) {

        leccionId?.let {

            lessonViewModel.loadLeccionById(it)

        }

    }



    Box(

        modifier = Modifier

            .fillMaxSize()

            .background(

                Brush.verticalGradient(

                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)

                )

            )

    ) {

        Scaffold(

            modifier = Modifier.fillMaxSize(),

            containerColor = Color.Transparent,

            topBar = {

                Box(

                    modifier = Modifier

                        .fillMaxWidth()

                        .height(100.dp)

                        .background(Color.Transparent)

                        .border(width = 1.dp, color = VeryDarkBlue),

                    contentAlignment = Alignment.Center

                ) {

                    Row(

                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),

                        verticalAlignment = Alignment.CenterVertically,

                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {

                        Image(

                            painter = painterResource(id = R.drawable.topbar),

                            contentDescription = "App Logo",

                            modifier = Modifier.height(70.dp),

                            contentScale = ContentScale.Fit,

                            alignment = Alignment.CenterStart

                        )

                        IconButton(

                            onClick = { navController.navigate("profile_screen") },

                            modifier = Modifier.size(70.dp)

                        ) {

                            val user = currentUserData

                            val avatarUrl = user?.avatarUrl



                            Box(

                                modifier = Modifier

                                    .size(70.dp)

                                    .clip(CircleShape)

                                    .background(Color.Transparent),

                                contentAlignment = Alignment.Center

                            ) {

                                val avatarDrawableNames = listOf(

                                    "avatar_1", "avatar_2", "avatar_3", "avatar_4", "avatar_5", "avatar_6"

                                )

                                val isLocalAvatar = avatarDrawableNames.contains(avatarUrl)

                                if (isLocalAvatar) {

                                    val drawableId = context.resources.getIdentifier(

                                        avatarUrl,

                                        "drawable",

                                        context.packageName

                                    )

                                    if (drawableId != 0) {

                                        Image(

                                            painter = painterResource(id = drawableId),

                                            contentDescription = "User Avatar",

                                            modifier = Modifier.fillMaxSize(),

                                            contentScale = ContentScale.Crop

                                        )

                                    } else {

                                        Icon(

                                            imageVector = Icons.Filled.Person,

                                            contentDescription = "Default Avatar",

                                            modifier = Modifier.fillMaxSize(),

                                            tint = VeryDarkBlue

                                        )

                                    }

                                } else if (avatarUrl != null && (avatarUrl.startsWith("gs://") || avatarUrl.startsWith("https://"))) {

                                    val finalImageUrl = avatarUrl.replace(

                                        "gs://ensenyas.firebasestorage.app/",

                                        "https://firebasestorage.googleapis.com/v0/b/ensenyas.firebasestorage.app/o/"

                                    ).replace("gs://ensenyas.appspot.com/", "https://firebasestorage.googleapis.com/v0/b/ensenyas.appspot.com/o/")

                                    val formattedImageUrl = if (finalImageUrl.contains("firebasestorage.googleapis.com") && !finalImageUrl.contains("?alt=media")) {

                                        "$finalImageUrl?alt=media"

                                    } else {

                                        finalImageUrl

                                    }

                                    AsyncImage(

                                        model = formattedImageUrl,

                                        contentDescription = "User Avatar",

                                        modifier = Modifier.fillMaxSize(),

                                        contentScale = ContentScale.Crop,

                                        error = rememberVectorPainter(Icons.Filled.Person),

                                        placeholder = rememberVectorPainter(Icons.Filled.Person)

                                    )

                                } else {

                                    Icon(

                                        imageVector = Icons.Filled.Person,

                                        contentDescription = "Default Avatar",

                                        modifier = Modifier.fillMaxSize(),

                                        tint = VeryDarkBlue

                                    )

                                }

                            }

                        }

                    }

                }

            }

        ) { paddingValues ->

            if (selectedLeccionState.value == null) {

                Box(

                    modifier = Modifier

                        .fillMaxSize()

                        .padding(paddingValues),

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = "Cargando detalles de la lección o lección no encontrada...",

                        color = VeryDarkBlue,

                        textAlign = TextAlign.Center,

                        modifier = Modifier.padding(16.dp)

                    )

                }

            } else {

                val leccionActual = selectedLeccionState.value!!

                LazyColumn(

                    modifier = Modifier

                        .fillMaxSize()

                        .padding(paddingValues)

                        .padding(horizontal = 16.dp),

                    verticalArrangement = Arrangement.spacedBy(12.dp)

                ) {

                    item {

                        Row(

                            modifier = Modifier

                                .fillMaxWidth()

                                .padding(vertical = 8.dp),

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            IconButton(

                                onClick = { navController.popBackStack() },

                                modifier = Modifier.size(40.dp)

                            ) {

                                Icon(

                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,

                                    contentDescription = "Atrás",

                                    tint = VeryDarkBlue,

                                    modifier = Modifier.size(28.dp)

                                )

                            }

                            Text(

                                text = "Lecciones",

                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),

                                modifier = Modifier.padding(start = 8.dp)

                            )

                        }



                        Text(

                            text = leccionActual.titulo,

                            style = MaterialTheme.typography.headlineLarge.copy(color = VeryDarkBlue),

                            textAlign = TextAlign.Center,

                            modifier = Modifier

                                .fillMaxWidth()

                                .padding(bottom = 8.dp)

                        )

                        Text(

                            text = leccionActual.descripcionCorta,

                            style = MaterialTheme.typography.bodyLarge.copy(color = VeryDarkBlue),

                            textAlign = TextAlign.Center,

                            modifier = Modifier

                                .fillMaxWidth()

                                .padding(bottom = 24.dp)

                        )

                        Text(

                            text = "Palabras:",

                            style = MaterialTheme.typography.headlineMedium.copy(color = VeryDarkBlue),

                            modifier = Modifier.padding(bottom = 12.dp)

                        )

                    }



                    items(leccionActual.temas.values.sortedBy { it.orden }) { tema ->

                        Card(

                            modifier = Modifier

                                .fillMaxWidth()

                                .clickable {

                                    navController.navigate("theme_detail/${leccionActual.idLeccion}/${tema.idTema}")

                                },

                            shape = MaterialTheme.shapes.medium,

                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),

                            colors = CardDefaults.cardColors(containerColor = Color.White),

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

                                    text = tema.titulo,

                                    style = MaterialTheme.typography.titleLarge,

                                    color = VeryDarkBlue,

                                    textAlign = TextAlign.Center

                                )

                                Text(

                                    text = tema.definicion,

                                    style = MaterialTheme.typography.bodyMedium,

                                    color = VeryDarkBlue,

                                    textAlign = TextAlign.Center

                                )

                            }

                        }

                    }



                    if (leccionActual.test != null) {

                        item {

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(

                                onClick = {

                                    navController.navigate("quiz_screen/${leccionActual.idLeccion}")

                                },

                                modifier = Modifier

                                    .fillMaxWidth()

                                    .height(56.dp),

                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),

                                colors = ButtonDefaults.buttonColors(

                                    containerColor = MaterialTheme.colorScheme.primary

                                )

                            ) {

                                Text("Realizar Test", style = MaterialTheme.typography.titleMedium)

                            }

                            Spacer(modifier = Modifier.height(24.dp))

                        }

                    }

                }

            }

        }

    }

}