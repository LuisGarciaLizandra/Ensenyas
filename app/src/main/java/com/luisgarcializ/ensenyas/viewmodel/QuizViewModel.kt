package com.luisgarcializ.ensenyas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.luisgarcializ.ensenyas.model.Leccion
import com.luisgarcializ.ensenyas.model.Tema
import com.luisgarcializ.ensenyas.model.Test
import com.luisgarcializ.ensenyas.model.Opcion
import com.luisgarcializ.ensenyas.model.ProgresoLeccion
import kotlin.random.Random
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuizViewModel(
    private val database: DatabaseReference = Firebase.database("https://ensenyas-default-rtdb.europe-west1.firebasedatabase.app").reference,
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _currentTest = MutableLiveData<Test?>()
    val currentTest: LiveData<Test?> = _currentTest

    private val _quizFeedback = MutableLiveData<String?>()
    val quizFeedback: LiveData<String?> = _quizFeedback

    private val _testPassed = MutableLiveData<Boolean?>()
    val testPassed: LiveData<Boolean?> = _testPassed

    private val _loadingError = MutableLiveData<String?>()
    val loadingError: LiveData<String?> = _loadingError

    private val _mediaUrl = MutableLiveData<String?>(null)
    val mediaUrl: LiveData<String?> = _mediaUrl

    private val _correctAnswerVideoUrl = MutableLiveData<String?>(null)
    val correctAnswerVideoUrl: LiveData<String?> = _correctAnswerVideoUrl

    private var currentLeccionId: String? = null
    private var temasLeccion: List<Tema> = emptyList()
    private var currentRandomTema: Tema? = null // Guardar el tema aleatorio seleccionado

    fun loadTestForLeccion(leccionId: String) {
        currentLeccionId = leccionId
        resetState()

        database.child("lecciones").child(leccionId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leccion = snapshot.getValue(Leccion::class.java) ?: run {
                        _loadingError.value = "Lección no encontrada."
                        return
                    }

                    if (leccion.idLeccion.isEmpty()) {
                        leccion.idLeccion = snapshot.key ?: ""
                    }

                    temasLeccion = leccion.temas?.values?.toList() ?: emptyList()
                    loadTest(leccionId)
                }

                override fun onCancelled(error: DatabaseError) {
                    _loadingError.value = "Error al cargar lección: ${error.message}"
                }
            }
        )
    }

    private fun loadTest(leccionId: String) {
        database.child("lecciones").child(leccionId).child("test")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val test = snapshot.getValue(Test::class.java) ?: run {
                        _loadingError.value = "Test no encontrado para esta lección."
                        return
                    }

                    _currentTest.value = test
                    selectRandomVideo()
                }

                override fun onCancelled(error: DatabaseError) {
                    _loadingError.value = "Error al cargar test: ${error.message}"
                }
            })
    }

    private fun selectRandomVideo() {
        if (temasLeccion.isEmpty()) {
            _loadingError.value = "No hay temas disponibles para esta lección."
            return
        }

        val randomTema = temasLeccion[Random.nextInt(temasLeccion.size)]
        currentRandomTema = randomTema // Guarda el tema seleccionado
        _correctAnswerVideoUrl.value = randomTema.videoUrl // Aunque no se muestre, se usa para la lógica

        val correctOption = Opcion(
            texto = randomTema.titulo,
            isCorrect = true,
            explicacionCorrecta = "¡Correcto! Este es el signo para '${randomTema.titulo}'.",
            explicacionIncorrecta = "Incorrecto. Este no es el signo para '${randomTema.titulo}'."
        )

        val incorrectOptions = temasLeccion
            .filter { it.idTema != randomTema.idTema }
            .shuffled()
            .take(3)
            .map { temaIncorrecto ->
                Opcion(
                    texto = temaIncorrecto.titulo,
                    isCorrect = false,
                    explicacionCorrecta = "",
                    explicacionIncorrecta = "No es correcto. El signo mostrado no corresponde a '${temaIncorrecto.titulo}'."
                )
            }

        val allOptions = (incorrectOptions + correctOption).shuffled()

        val optionsMap = mutableMapOf<String, Opcion>()
        for (i in allOptions.indices) {
            optionsMap["opcion${i + 1}"] = allOptions[i]
        }

        _currentTest.value = Test(
            idTest = _currentTest.value?.idTest ?: "generated",
            pregunta = "¿Qué expresión representa este signo?",
            tipoPregunta = "multiple_choice",
            signoUrl = _currentTest.value?.signoUrl ?: "",
            opciones = optionsMap
        )

        processSignUrl(randomTema.videoUrl)
    }

    fun processSignUrl(videoUrl: String) { // Ahora pública para reintentar carga de video
        val storage = Firebase.storage
        _loadingError.value = null // Limpiar error antes de intentar cargar

        viewModelScope.launch {
            try {
                val finalUrl = when {
                    videoUrl.startsWith("https://") -> videoUrl
                    videoUrl.startsWith("gs://") -> {
                        val path = when {
                            videoUrl.startsWith("gs://ensenyas.appspot.com/") ->
                                videoUrl.removePrefix("gs://ensenyas.appspot.com/")
                            videoUrl.startsWith("gs://ensenyas.firebasestorage.app/") ->
                                videoUrl.removePrefix("gs://ensenyas.firebasestorage.app/")
                            else -> videoUrl.substringAfter("gs://")
                        }
                        storage.reference.child(path).downloadUrl.await().toString()
                    }
                    else -> throw Exception("Formato de URL no soportado: $videoUrl")
                }
                _mediaUrl.value = finalUrl
            } catch (e: Exception) {
                handleError("Error al cargar video: ${e.message}")
            }
        }
    }

    fun submitQuizAnswer(selectedOptionId: String) {
        val test = _currentTest.value ?: run {
            handleError("Test no cargado")
            return
        }

        val selectedOption = test.opciones[selectedOptionId] ?: run {
            _quizFeedback.value = "Opción seleccionada no válida."
            _testPassed.value = false
            return
        }

        if (selectedOption.isCorrect) {
            handleCorrectAnswer(selectedOption.explicacionCorrecta)
        } else {
            handleWrongAnswer(selectedOption.explicacionIncorrecta)
        }
    }

    private fun handleCorrectAnswer(feedback: String) {
        _quizFeedback.value = feedback
        _testPassed.value = true

        currentLeccionId?.let { leccionId ->
            Log.d("QuizViewModel", "Iniciando actualización de progreso para lección: $leccionId")
            updateUserProgress(leccionId, true)
                .addOnSuccessListener {
                    Log.d("QuizViewModel", "Progreso de lección $leccionId actualizado exitosamente. Intentando desbloquear siguiente.")
                    unlockNextLesson(leccionId)
                }
                .addOnFailureListener { e ->
                    Log.e("QuizViewModel", "Fallo al actualizar progreso de lección $leccionId: ${e.message}", e)
                    _loadingError.postValue("Error al guardar progreso: ${e.message}")
                }
        }
    }

    private fun handleWrongAnswer(feedback: String) {
        _quizFeedback.value = feedback
        _testPassed.value = false
    }

    fun resetQuizState() { // Renombrada de resetQuizState para mayor claridad
        resetState()
    }

    fun reattemptQuiz() { // Nueva función para reintentar la misma pregunta
        _quizFeedback.value = null
        _testPassed.value = null
        _loadingError.value = null // Limpiar errores si los hubo
        // NO TOCAR _mediaUrl o _correctAnswerVideoUrl para mantener la misma pregunta
        // Esto solo restablece el estado de respuesta
        if (currentRandomTema != null) {
            // Re-generar opciones si es necesario o simplemente asegurar que currentTest no es nulo
            // Para mantener la misma pregunta, solo necesitamos re-exponer _currentTest.value
            // y resetear el proceso de feedback.
            _currentTest.value = _currentTest.value // Notificar a los observadores sin cambiar el contenido
            processSignUrl(currentRandomTema!!.videoUrl) // Reintentar carga del video si falló
        } else {
            _loadingError.value = "No se pudo reintentar el quiz. Falta información del tema."
        }
    }


    private fun resetState() {
        _quizFeedback.value = null
        _testPassed.value = null
        _loadingError.value = null
        _mediaUrl.value = null
        _correctAnswerVideoUrl.value = null
        currentRandomTema = null // Limpiar el tema actual al cargar un nuevo test
    }

    private fun updateUserProgress(leccionId: String, completed: Boolean): com.google.android.gms.tasks.Task<Void> {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado al actualizar progreso.")

        val progressUpdate = hashMapOf<String, Any>(
            "completada" to completed
        )

        return database.child("usuarios").child(userId).child("progreso").child(leccionId)
            .updateChildren(progressUpdate)
    }


    private fun unlockNextLesson(currentLeccionId: String) {
        val currentUserUid = auth.currentUser?.uid ?: return

        database.child("lecciones").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("QuizViewModel", "Cargando todas las lecciones para desbloqueo...")
                val allLessons = snapshot.children.mapNotNull {
                    it.getValue(Leccion::class.java)?.apply {
                        if (idLeccion.isEmpty()) {
                            idLeccion = it.key ?: ""
                        }
                    }
                }.sortedBy { it.orden }

                Log.d("QuizViewModel", "Lecciones cargadas (${allLessons.size}): ${allLessons.map { "${it.idLeccion} (Orden: ${it.orden})" }}")

                val currentLesson = allLessons.find { it.idLeccion == currentLeccionId }
                Log.d("QuizViewModel", "Lección actual (${currentLeccionId}): ${currentLesson?.titulo} (Orden: ${currentLesson?.orden})")

                val nextLesson = allLessons.filter { it.orden > (currentLesson?.orden ?: Int.MAX_VALUE) }
                    .minByOrNull { it.orden }

                if (nextLesson != null) {
                    Log.d("QuizViewModel", "Siguiente lección encontrada: ${nextLesson.titulo} (Orden: ${nextLesson.orden}, ID: ${nextLesson.idLeccion})")
                    val nextLeccionId = nextLesson.idLeccion
                    database.child("lecciones").child(nextLeccionId)
                        .child("isLocked").setValue(false)
                        .addOnSuccessListener {
                            Log.d("QuizViewModel", "Lección $nextLeccionId desbloqueada exitosamente en DB")
                        }
                        .addOnFailureListener { e ->
                            Log.e("QuizViewModel", "Error al desbloquear lección $nextLeccionId en DB: ${e.message}", e)
                            _loadingError.postValue("No se pudo desbloquear la siguiente lección. ${e.message}")
                        }
                } else {
                    Log.d("QuizViewModel", "No hay siguiente lección para desbloquear después de $currentLeccionId. (Es la última lección o no se encontró sucesora)")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuizViewModel", "Error al buscar siguiente lección para desbloquear: ${error.message}", error.toException())
                _loadingError.postValue("Error al buscar la siguiente lección. ${error.message}")
            }
        })
    }

    private fun handleError(message: String) {
        _loadingError.value = message
        Log.e("QuizViewModel", message)
    }
}