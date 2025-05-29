
package com.luisgarcializ.ensenyas.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.util.Log
import com.luisgarcializ.ensenyas.model.Configuracion
import com.luisgarcializ.ensenyas.model.Usuario

class AuthViewModel (
    private val auth: FirebaseAuth = Firebase.auth,
    private val database: DatabaseReference = Firebase.database("https://ensenyas-default-rtdb.europe-west1.firebasedatabase.app").reference
) : ViewModel(){



    fun registerUser(email: String, password: String, username: String, birthDate: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val firebaseUser = authTask.result?.user
                    firebaseUser?.let { user ->
                        val newUser = Usuario(
                            email = email,
                            username = username,
                            fechaNacimiento = birthDate,
                            avatarUrl = "gs://ensenyas.appspot.com/avatars/default.png",
                            progreso = emptyMap(),
                            configuracion = Configuracion(modoMinimalista = false)
                        )
                        database.child("usuarios").child(user.uid).setValue(newUser)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Log.d("AuthViewModel", "Registro de usuario exitoso.")
                                    onComplete(true, null) // Éxito, no hay mensaje de error
                                } else {
                                    val dbErrorMessage = dbTask.exception?.message ?: "Error desconocido al guardar datos de usuario."
                                    Log.e("AuthViewModel", "Fallo al registrar usuario en la base de datos: $dbErrorMessage")
                                    onComplete(false, dbErrorMessage) // Fallo al guardar en DB
                                }
                            }
                    } ?: run {
                        Log.e("AuthViewModel", "Usuario Firebase nulo después del registro.")
                        onComplete(false, "Error desconocido al registrar usuario: usuario nulo.") // Error si firebaseUser es nulo
                    }
                } else {
                    val authErrorMessage = authTask.exception?.message ?: "Error desconocido en el registro de autenticación."
                    Log.e("AuthViewModel", "Fallo en el registro de autenticación: $authErrorMessage")
                    onComplete(false, authErrorMessage) // Fallo en la autenticación (email ya en uso, contraseña débil, etc.)
                }
            }
    }



    fun signInUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Inicio de sesión exitoso para ${task.result?.user?.email}")
                    onComplete(true, null)
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido al iniciar sesión."
                    Log.e("AuthViewModel", "Fallo al iniciar sesión: $errorMessage")
                    onComplete(false, errorMessage)
                }
            }
    }

}


