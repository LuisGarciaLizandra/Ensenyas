package com.luisgarcializ.ensenyas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.luisgarcializ.ensenyas.ui.theme.EnsenyasTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseAuth
import com.luisgarcializ.ensenyas.ui.lessons.LessonsListScreen
import com.luisgarcializ.ensenyas.ui.auth.SignInScreen
import com.luisgarcializ.ensenyas.ui.auth.SignUpScreen
import com.luisgarcializ.ensenyas.ui.auth.WelcomeScreen
import com.luisgarcializ.ensenyas.ui.lessons.LessonDetailScreen
import com.luisgarcializ.ensenyas.ui.lessons.ThemeDetailScreen
import com.luisgarcializ.ensenyas.ui.lessons.QuizScreen
import com.luisgarcializ.ensenyas.ui.auth.ProfileScreen
import com.luisgarcializ.ensenyas.ui.auth.AvatarSelectionScreen

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (FirebaseApp.getApps(this).isEmpty()){
            val options = FirebaseOptions.Builder()
                .setApiKey("AIzaSyDCGg2Ozcs3e5cvEHLhHHhr2-6dB2AHJck")
                .setApplicationId("com.luisgarcializ.ensenyas")
                .setDatabaseUrl("https://ensenyas-default-rtdb.europe-west1.firebasedatabase.app")
                .setStorageBucket("ensenyas.firebasestorage.app")
                .setProjectId("ensenyas")
                .build()
            FirebaseApp.initializeApp(this, options)
        }

        auth = com.google.firebase.ktx.Firebase.auth
        setContent {
            EnsenyasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    )
                    {
                        EnsenyasApp(auth = auth)
                    }
                }
            }
        }
    }

    @Composable
    fun EnsenyasApp(auth: FirebaseAuth) {
        val currentUser = auth.currentUser
        val navController = rememberNavController()
        var startDestination by remember { mutableStateOf("")}

        startDestination = if (currentUser != null) {
            "lessons_list"
        }else{
            "welcome"
        }

        if (startDestination.isNotEmpty()){
            NavHost(navController = navController, startDestination = startDestination){
                composable("welcome"){
                    WelcomeScreen(navController = navController, auth = auth)
                }
                composable("lessons_list"){
                    LessonsListScreen(navController = navController, auth = auth)
                }
                composable("sign_in"){
                    SignInScreen(navController = navController, auth = auth)
                }
                composable("sign_up") {
                    SignUpScreen(navController = navController, auth = auth)
                }
                composable("lesson_detail/{leccionId}") { backStackEntry ->
                    val leccionId = backStackEntry.arguments?.getString("leccionId")
                    LessonDetailScreen(navController = navController, auth = auth, leccionId = leccionId)
                }
                composable("theme_detail/{leccionId}/{temaId}") { backStackEntry ->
                    val leccionId = backStackEntry.arguments?.getString("leccionId")
                    val temaId = backStackEntry.arguments?.getString("temaId")
                    ThemeDetailScreen(navController = navController, auth = auth, leccionId = leccionId, temaId = temaId)
                }
                composable("quiz_screen/{leccionId}") { backStackEntry ->
                    val leccionId = backStackEntry.arguments?.getString("leccionId")
                    QuizScreen(navController = navController, auth = auth, leccionId = leccionId)
                }
                composable("profile_screen"){
                    ProfileScreen(navController = navController, auth = auth)
                }

                composable("avatar_selection_screen?fromSignup={fromSignup}") { backStackEntry ->
                    val fromSignup = backStackEntry.arguments?.getString("fromSignup")?.toBoolean() ?: false
                    AvatarSelectionScreen(navController = navController, auth = auth, fromSignup = fromSignup)
                }
            }
        }
    }
}