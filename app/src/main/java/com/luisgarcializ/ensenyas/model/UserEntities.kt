package com.luisgarcializ.ensenyas.model

data class Usuario(
    val email: String = "",
    val username: String = "",
    val fechaNacimiento: String = "",
    val avatarUrl: String = "",
    val progreso: Map<String, ProgresoLeccion> = emptyMap(),
    val configuracion: Configuracion = Configuracion()
)

data class ProgresoLeccion(
    val completada: Boolean = false,
    val temasCompletados: Map<String, Boolean> = emptyMap()
)

data class Configuracion(
    val modoMinimalista: Boolean = false
)



