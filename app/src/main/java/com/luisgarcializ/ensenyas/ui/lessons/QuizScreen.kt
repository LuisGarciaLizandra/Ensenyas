package com.luisgarcializ.ensenyas.ui.lessons

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.luisgarcializ.ensenyas.R
import com.luisgarcializ.ensenyas.ui.common.ErrorView
import com.luisgarcializ.ensenyas.ui.common.LoadingView
import com.luisgarcializ.ensenyas.ui.common.VideoPlayer
import com.luisgarcializ.ensenyas.viewmodel.QuizViewModel
import com.luisgarcializ.ensenyas.viewmodel.UserProfileViewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role

private val VeryDarkBlue = Color(0xFF00008B)
private val BackgroundGradientStart = Color(0xFFE3F2FD)
private val BackgroundGradientEnd = Color(0xFFBBDEFB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    auth: FirebaseAuth,
    leccionId: String?,
    quizViewModel: QuizViewModel = viewModel(),
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTest by quizViewModel.currentTest.observeAsState()
    val quizFeedback by quizViewModel.quizFeedback.observeAsState()
    val testPassed by quizViewModel.testPassed.observeAsState()
    val loadingError by quizViewModel.loadingError.observeAsState()
    val mediaUrl by quizViewModel.mediaUrl.observeAsState()
    val correctAnswerVideoUrl by quizViewModel.correctAnswerVideoUrl.observeAsState() // Aunque no se muestre, lo mantenemos por si la lógica interna de ViewModel lo usa
    val currentUserData by userProfileViewModel.currentUserData.observeAsState()

    var selectedOptionId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(leccionId) {
        if (leccionId != null) {
            quizViewModel.loadTestForLeccion(leccionId)
        } else {
            Toast.makeText(context, "ID de lección no proporcionado", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(testPassed) {
        testPassed?.let { passed ->
            val message = quizFeedback ?: if (passed) "¡Test superado!" else "Respuesta incorrecta"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
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
                                    .background(Color.White),
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    loadingError != null -> {
                        ErrorView(loadingError)
                        Button(
                            onClick = { leccionId?.let { quizViewModel.loadTestForLeccion(it) } },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                    currentTest == null -> {
                        LoadingView()
                    }
                    else -> {
                        Log.d("QuizScreenVideo", "Media URL en QuizScreen: $mediaUrl")

                        // El video del signo se muestra siempre
                        mediaUrl?.let { url ->
                            VideoPlayer(
                                videoUrl = url,
                                context = context,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .padding(bottom = 16.dp)
                            )
                        } ?: run { // Si mediaUrl es nulo (ej. error de carga inicial del video)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .padding(bottom = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Error al cargar el video. Por favor, intente de nuevo.", color = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = {
                                    quizViewModel.currentTest.value?.let {
                                        val temaUrl = quizViewModel.correctAnswerVideoUrl.value // Usar la URL del tema original
                                        if (temaUrl != null) {
                                            quizViewModel.processSignUrl(temaUrl)
                                        } else {
                                            // Si por alguna razón no tenemos la URL del tema original, recargar todo el test
                                            leccionId?.let { quizViewModel.loadTestForLeccion(it) }
                                        }
                                    }
                                }) {
                                    Text("Recargar Video")
                                }
                            }
                        }


                        Text(
                            text = currentTest!!.pregunta,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Column(Modifier.selectableGroup()) {
                            currentTest!!.opciones.entries.sortedBy { it.key }.forEach { (optionId, option) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .selectable(
                                            selected = (optionId == selectedOptionId),
                                            onClick = {
                                                if (testPassed == null) {
                                                    selectedOptionId = optionId
                                                }
                                            },
                                            role = Role.RadioButton,
                                            enabled = testPassed == null
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (optionId == selectedOptionId),
                                        onClick = null,
                                        enabled = testPassed == null
                                    )
                                    Text(
                                        text = option.texto,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        when (testPassed) {
                            null -> {
                                Button(
                                    onClick = {
                                        selectedOptionId?.let {
                                            quizViewModel.submitQuizAnswer(it)
                                        } ?: Toast.makeText(
                                            context,
                                            "Selecciona una opción",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    enabled = selectedOptionId != null
                                ) {
                                    Text("Enviar Respuesta")
                                }
                            }
                            true -> {
                                Text(
                                    text = "¡Test superado!",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("lessons_list") {
                                            popUpTo("lessons_list") { inclusive = true }
                                        }
                                    }
                                ) {
                                    Text("Siguiente Lección")
                                }
                            }
                            false -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Respuesta incorrecta",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Button(
                                        onClick = {
                                            selectedOptionId = null
                                            quizViewModel.reattemptQuiz() // Llama a la nueva función
                                        },
                                        modifier = Modifier.padding(top = 16.dp)
                                    ) {
                                        Text("Intentar de nuevo")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}