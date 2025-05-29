package com.luisgarcializ.ensenyas.ui.lessons

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.luisgarcializ.ensenyas.model.Leccion
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientEnd
import com.luisgarcializ.ensenyas.ui.theme.BackgroundGradientStart
import com.luisgarcializ.ensenyas.ui.theme.VeryDarkBlue
import com.luisgarcializ.ensenyas.ui.theme.OffWhite
import com.luisgarcializ.ensenyas.viewmodel.LessonViewModel
import com.luisgarcializ.ensenyas.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsListScreen(
    navController: NavController,
    auth: FirebaseAuth,
    lessonViewModel: LessonViewModel = viewModel(),
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val lecciones by lessonViewModel.lecciones.observeAsState(initial = emptyList())
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.topbar),
                            contentDescription = "App Logo",
                            modifier = Modifier.height(70.dp),
                            contentScale = ContentScale.Fit
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
                                    ).replace(
                                        "gs://ensenyas.appspot.com/",
                                        "https://firebasestorage.googleapis.com/v0/b/ensenyas.appspot.com/o/"
                                    )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (lecciones.isEmpty()) {
                    item {
                        Text(
                            text = "Cargando lecciones...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = VeryDarkBlue
                        )
                    }
                } else {
                    items(lecciones) { leccion ->
                        val isLocked = leccion.isLocked
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (isLocked) 0.4f else 1f)
                                .clickable(enabled = !isLocked) {
                                    if (!leccion.isLocked) {
                                        navController.navigate("lesson_detail/${leccion.idLeccion}")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "¡Lección bloqueada! Completa la lección anterior.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!isLocked) Color.White else OffWhite
                            ),
                            border = BorderStroke(width = 1.dp, color = VeryDarkBlue)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = leccion.titulo,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = VeryDarkBlue,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = leccion.descripcionCorta,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VeryDarkBlue,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
