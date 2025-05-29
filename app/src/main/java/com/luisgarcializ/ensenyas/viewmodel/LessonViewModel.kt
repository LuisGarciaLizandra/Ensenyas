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

class LessonViewModel(
    private val database: DatabaseReference = Firebase.database("https://ensenyas-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _lecciones = MutableLiveData<List<Leccion>>()
    val lecciones: LiveData<List<Leccion>> = _lecciones

    private val _selectedLeccion = MutableLiveData<Leccion?>()
    val selectedLeccion: LiveData<Leccion?> = _selectedLeccion

    private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    private var userProgress: Map<String, ProgresoLeccion> = emptyMap()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            loadUserProgress()
            loadLessons()
        }
        loadUserProgress()
        loadLessons()
    }

    private fun loadLessons() {
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
                val processedLessons = applyLessonLocking(loadedLessons.sortedBy { it.orden })
                _lecciones.value = processedLessons
                Log.d("LessonViewModel", "Lecciones cargadas: ${_lecciones.value?.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LessonViewModel", "Error al cargar lecciones", error.toException())
                _lecciones.value = emptyList()
            }
        })
    }

    fun loadLeccionById(leccionId: String) {
        _selectedLeccion.value = null

        database.child("lecciones").child(leccionId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leccion = snapshot.getValue(Leccion::class.java)
                    leccion?.let {
                        if (it.idLeccion.isEmpty()) {
                            it.idLeccion = snapshot.key ?: ""
                        }
                    }
                    _selectedLeccion.value = leccion
                    Log.d("LessonViewModel", "Lección $leccionId ${if (leccion != null) "cargada" else "no encontrada"}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("LessonViewModel", "Error al cargar lección $leccionId", error.toException())
                    _selectedLeccion.value = null
                }
            }
        )
    }

    private fun loadUserProgress() {
        val userId = auth.currentUser?.uid ?: return
        database.child("usuarios").child(userId).child("progreso")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val progressMap = mutableMapOf<String, ProgresoLeccion>()
                    for (progresoSnapshot in snapshot.children) {
                        val leccionId = progresoSnapshot.key ?: continue
                        val completada = progresoSnapshot.child("completada").getValue(Boolean::class.java) ?: false
                        val temasCompletadosMap = progresoSnapshot.child("temasCompletados").value as? Map<String, Boolean> ?: emptyMap()
                        progressMap[leccionId] = ProgresoLeccion(completada, temasCompletadosMap)
                    }
                    userProgress = progressMap
                    _lecciones.value?.let { currentLessons ->
                        _lecciones.value = applyLessonLocking(currentLessons.sortedBy { it.orden })
                    }
                    Log.d("LessonViewModel", "Progreso de usuario cargado: $userProgress")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("LessonViewModel", "Error al cargar progreso del usuario", error.toException())
                    userProgress = emptyMap()
                }
            })
    }

    private fun applyLessonLocking(lessons: List<Leccion>): List<Leccion> {
        val updatedLessons = lessons.toMutableList()
        var previousLessonCompleted = true

        for (i in updatedLessons.indices) {
            val leccion = updatedLessons[i]
            val leccionId = leccion.idLeccion
            val progresoLeccion = userProgress[leccionId]

            if (leccion.orden == 1) {
                leccion.isLocked = false
            } else {
                leccion.isLocked = !previousLessonCompleted
            }

            previousLessonCompleted = progresoLeccion?.completada ?: false
        }
        return updatedLessons
    }
}