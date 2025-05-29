package com.luisgarcializ.ensenyas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log
import com.luisgarcializ.ensenyas.model.Leccion
import com.google.firebase.auth.FirebaseUser
import com.luisgarcializ.ensenyas.model.Tema
import com.luisgarcializ.ensenyas.model.ProgresoLeccion
import com.luisgarcializ.ensenyas.model.Usuario
import com.luisgarcializ.ensenyas.model.Configuracion

class UserProfileViewModel(
    private val database: DatabaseReference = Firebase.database("https://ensenyas-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _currentUserData = MutableLiveData<Usuario?>()
    val currentUserData: LiveData<Usuario?> = _currentUserData

    private val _userProgress = MutableLiveData<Map<String, ProgresoLeccion>>()
    val userProgress: LiveData<Map<String, ProgresoLeccion>> = _userProgress

    private val _allLessons = MutableLiveData<List<Leccion>>()
    val allLessons: LiveData<List<Leccion>> = _allLessons

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                loadUserData(firebaseUser.uid)
                loadUserProgress(firebaseUser.uid)
                loadAllLessons()
            } else {
                _currentUserData.value = null
                _userProgress.value = emptyMap()
                _isLoading.value = false
                _errorMessage.value = "Usuario no autenticado."
            }
        }
        auth.currentUser?.uid?.let { uid ->
            loadUserData(uid)
            loadUserProgress(uid)
            loadAllLessons()
        } ?: run {
            _isLoading.value = false
            _errorMessage.value = "Usuario no autenticado al iniciar ViewModel."
        }
    }

    private fun loadUserData(uid: String) {
        _isLoading.value = true
        database.child("usuarios").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Usuario::class.java)
                _currentUserData.value = user
                _isLoading.value = false
                Log.d("UserProfileViewModel", "Datos de usuario cargados: ${user?.username}")
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error al cargar datos de usuario: ${error.message}"
                _isLoading.value = false
                Log.e("UserProfileViewModel", "Error al cargar datos de usuario", error.toException())
            }
        })
    }

    private fun loadUserProgress(uid: String) {
        database.child("usuarios").child(uid).child("progreso").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val progressMap = mutableMapOf<String, ProgresoLeccion>()
                for (progresoSnapshot in snapshot.children) {
                    val leccionId = progresoSnapshot.key ?: continue
                    val completada = progresoSnapshot.child("completada").getValue(Boolean::class.java) ?: false
                    val temasCompletadosMap = progresoSnapshot.child("temasCompletados").value as? Map<String, Boolean> ?: emptyMap()
                    progressMap[leccionId] = ProgresoLeccion(completada, temasCompletadosMap)
                }
                _userProgress.value = progressMap
                Log.d("UserProfileViewModel", "Progreso de usuario cargado: ${progressMap.size} lecciones")
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error al cargar progreso: ${error.message}"
                Log.e("UserProfileViewModel", "Error al cargar progreso", error.toException())
            }
        })
    }

    private fun loadAllLessons() {
        database.child("lecciones").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedLessons = mutableListOf<Leccion>()
                for (leccionSnapshot in snapshot.children) {
                    val leccion = leccionSnapshot.getValue(Leccion::class.java)
                    leccion?.let {
                        if (it.idLeccion.isEmpty()) {
                            it.idLeccion = leccionSnapshot.key ?: ""
                        }
                        loadedLessons.add(it)
                    }
                }
                _allLessons.value = loadedLessons.sortedBy { it.orden }
                Log.d("UserProfileViewModel", "Todas las lecciones cargadas: ${_allLessons.value?.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error al cargar todas las lecciones: ${error.message}"
                Log.e("UserProfileViewModel", "Error al cargar todas las lecciones", error.toException())
            }
        })
    }

    fun updateMinimalistMode(isMinimalist: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        database.child("usuarios").child(userId).child("configuracion").child("modoMinimalista")
            .setValue(isMinimalist)
            .addOnSuccessListener {
                Log.d("UserProfileViewModel", "Modo minimalista actualizado a: $isMinimalist")
                loadUserData(userId)
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Error al actualizar modo minimalista: ${e.message}"
                Log.e("UserProfileViewModel", "Error al actualizar modo minimalista", e)
            }
    }

    // --- NUEVA FUNCIÓN PARA ACTUALIZAR EL AVATAR DEL USUARIO ---
    fun updateUserAvatar(avatarUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        database.child("usuarios").child(userId).child("avatarUrl").setValue(avatarUrl)
            .addOnSuccessListener {
                Log.d("UserProfileViewModel", "Avatar del usuario actualizado a: $avatarUrl")
                loadUserData(userId) // Recargar datos para que la UI se actualice
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Error al actualizar avatar: ${e.message}"
                Log.e("UserProfileViewModel", "Error al actualizar avatar", e)
            }
    }


    fun signOut() {
        auth.signOut()
        _currentUserData.value = null
        _userProgress.value = emptyMap()
        _isLoading.value = false
        _errorMessage.value = null
    }
}