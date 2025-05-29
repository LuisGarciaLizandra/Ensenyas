package com.luisgarcializ.ensenyas.ui.lessons

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.StorageException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisgarcializ.ensenyas.viewmodel.LessonViewModel
import com.luisgarcializ.ensenyas.model.Leccion
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.luisgarcializ.ensenyas.R
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientEnd
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientStart
import com.luisgarcializ.ensenyas.ui.theme.VeryDarkBlue
import com.luisgarcializ.ensenyas.ui.theme.VeryLightBlueButton
import com.luisgarcializ.ensenyas.viewmodel.UserProfileViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDetailScreen(
    navController: NavController,
    leccionId: String?,
    temaId: String?,
    auth: FirebaseAuth,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val lessonViewModel: LessonViewModel = viewModel()
    val context = LocalContext.current
    val storage = Firebase.storage
    val currentUserData by userProfileViewModel.currentUserData.observeAsState()

    var downloadUrl by remember { mutableStateOf<String?>(null) }
    var loadingError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val selectedLeccion by lessonViewModel.selectedLeccion.observeAsState()

    LaunchedEffect(leccionId) {
        Log.d("ThemeDetailScreen", "Recibido leccionId: $leccionId")
        if (leccionId != null) {
            isLoading = true
            lessonViewModel.loadLeccionById(leccionId)
        } else {
            errorMessage = "ID de lección no proporcionado"
            loadingError = true
            isLoading = false
        }
    }

    LaunchedEffect(selectedLeccion, temaId) {
        val currentLeccion = selectedLeccion
        if (currentLeccion != null && temaId != null) {
            Log.d("ThemeDetailScreen", "Cargando temaId: $temaId para lección: ${currentLeccion.idLeccion}")
            try {
                val tema = currentLeccion.temas[temaId] ?: currentLeccion.temas.values
                    .firstOrNull { it.idTema == temaId }

                if (tema != null) {
                    Log.d("ThemeDetailScreen", "Tema encontrado: ${tema.idTema}")
                    downloadUrl = when {
                        tema.videoUrl.startsWith("https://") -> tema.videoUrl
                        tema.videoUrl.startsWith("gs://") -> {
                            Log.d("ThemeDetailScreen", "URL GS detectada: ${tema.videoUrl}")
                            if (auth.currentUser == null) {
                                throw Exception("Debes iniciar sesión para ver este contenido")
                            }
                            try {
                                val path = tema.videoUrl.removePrefix("gs://${storage.reference.bucket}/")
                                Log.d("StoragePath", "Accediendo a: $path")
                                storage.reference.child(path).downloadUrl.await().toString()
                            } catch (e: StorageException) {
                                throw when (e.errorCode) {
                                    StorageException.ERROR_NOT_AUTHORIZED ->
                                        Exception("No tienes permiso para este video")
                                    StorageException.ERROR_OBJECT_NOT_FOUND ->
                                        Exception("Video no encontrado en: ${tema.videoUrl}")
                                    else -> Exception("Error al cargar video: ${e.message}")
                                }
                            }
                        }
                        else -> throw Exception("Formato de video no soportado")
                    }
                    loadingError = false
                    errorMessage = null
                } else {
                    Log.e("ThemeDetailScreen", "Tema no encontrado. Keys disponibles: ${currentLeccion.temas?.keys}")
                    Log.e("ThemeDetailScreen", "Tema ID recibido: $temaId")
                    throw Exception("Tema no encontrado")
                }
            } catch (e: Exception) {
                errorMessage = e.message
                loadingError = true
                Log.e("ThemeDetailScreen", "Error", e)
            } finally {
                isLoading = false
            }
        } else if (currentLeccion == null && leccionId != null) {
            isLoading = true
            Log.d("ThemeDetailScreen", "Esperando que se cargue la lección $leccionId...")
        } else {
            isLoading = false
            errorMessage = "No se pudo cargar el tema. Lección o tema no válido."
            loadingError = true
        }
    }

    if (loadingError && errorMessage?.contains("sesión") == true) {
        AuthRequiredScreen(errorMessage) { navController.navigate("sign_in") }
    } else {
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
                                                imageVector = Icons.Default.Person,
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
                                            error = rememberVectorPainter(Icons.Default.Person),
                                            placeholder = rememberVectorPainter(Icons.Default.Person)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(color = Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when {
                        isLoading -> LoadingView()
                        loadingError -> ErrorView(errorMessage)
                        downloadUrl != null -> ContentView(
                            selectedLeccion = selectedLeccion,
                            temaId = temaId,
                            videoUrl = downloadUrl!!,
                            context = context,
                            navController = navController
                        )
                        else -> EmptyView()
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthRequiredScreen(message: String?, onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Advertencia",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message ?: "Acceso requerido",
            style = MaterialTheme.typography.bodyLarge.copy(color = VeryDarkBlue),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLoginClick) {
            Text("Iniciar sesión")
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Cargando...", color = VeryDarkBlue)
    }
}

@Composable
private fun ErrorView(message: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message ?: "Error desconocido",
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ContentView(
    selectedLeccion: Leccion?,
    temaId: String?,
    videoUrl: String,
    context: Context,
    navController: NavController
) {
    val tema = selectedLeccion?.temas?.entries
        ?.firstOrNull { it.key == temaId || it.value.idTema == temaId }
        ?.value

    if (tema == null) {
        Log.e("ThemeDetailScreen", "ContenidoView: Tema no encontrado. TemaId: $temaId, Keys: ${selectedLeccion?.temas?.keys}")
        ErrorView("No se pudo cargar la información del tema.")
        return
    }

    val temasSorted = selectedLeccion?.temas?.values?.sortedBy { it.orden }
    val currentTemaIndex = temasSorted?.indexOfFirst { it.idTema == tema.idTema }
    val nextTema = if (currentTemaIndex != null && currentTemaIndex < temasSorted.size - 1) {
        temasSorted[currentTemaIndex + 1]
    } else {
        null
    }
    val hasNextTema = nextTema != null

    val previousTema = if (currentTemaIndex != null && currentTemaIndex > 0) {
        temasSorted[currentTemaIndex - 1]
    } else {
        null
    }
    val hasPreviousTema = previousTema != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(color = Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = tema.titulo,
                style = MaterialTheme.typography.headlineSmall.copy(color = VeryDarkBlue),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            VideoPlayer(
                videoUrl = videoUrl,
                context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = tema.definicion,
                style = MaterialTheme.typography.bodyMedium.copy(color = VeryDarkBlue),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    previousTema?.let {
                        navController.navigate("theme_detail/${selectedLeccion?.idLeccion}/${it.idTema}")
                    }
                },
                enabled = hasPreviousTema,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VeryLightBlueButton,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Tema Anterior", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = {
                    nextTema?.let {
                        navController.navigate("theme_detail/${selectedLeccion?.idLeccion}/${it.idTema}")
                    }
                },
                enabled = hasNextTema,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VeryLightBlueButton,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Siguiente Tema", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = {
                    selectedLeccion?.idLeccion?.let { id ->
                        navController.navigate("quiz_screen/$id")
                    } ?: Toast.makeText(context, "No hay test disponible para esta lección", Toast.LENGTH_SHORT).show()
                },
                enabled = selectedLeccion?.test != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Realizar Test", style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun VideoPlayer(
    videoUrl: String,
    context: Context,
    modifier: Modifier = Modifier
) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
            prepare()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = modifier
    )
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No hay contenido disponible", color = VeryDarkBlue)
    }
}