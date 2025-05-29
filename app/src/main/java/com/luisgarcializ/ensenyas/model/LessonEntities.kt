package com.luisgarcializ.ensenyas.model

data class Leccion(
    var idLeccion: String = "",
    var isLocked: Boolean = false,
    var titulo: String = "",
    var descripcionCorta: String = "",
    var orden: Int = 0,
    var temas: Map<String, Tema> = emptyMap(), // Cambiado a var
    var test: Test? = null
) {

    constructor() : this("", false, "", "", 0, emptyMap(), null)
}

data class Tema(
    var idTema: String = "",
    var titulo: String = "",
    var definicion: String = "",
    var iconoUrl: String = "",
    var videoUrl: String = "",
    var orden: Int = 0
) {
    constructor() : this("", "", "", "", "", 0)
}

data class Test(
    var idTest: String = "",
    var pregunta: String = "",
    var tipoPregunta: String = "",
    var signoUrl: String = "",
    var opciones: Map<String, Opcion> = emptyMap() // Cambiado a var
) {
    constructor() : this("", "", "", "", emptyMap())
}

data class Opcion(
    var texto: String = "",
    var isCorrect: Boolean = false, // Cambiado a var, valor por defecto explícito
    var explicacionCorrecta: String = "",
    var explicacionIncorrecta: String = ""
) {
    constructor() : this("", false, "", "")
}



