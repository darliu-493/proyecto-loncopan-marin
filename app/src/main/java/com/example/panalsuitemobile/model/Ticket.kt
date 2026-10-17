package com.example.panalsuitemobile.model

data class Ticket(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val prioridad: Prioridad,
    val estado: Estado,
    val solicitante: String,
    val resolutor: String,
    val fechaCreacion: String,
    val fechaActualizacion: String,
    val comentarios: MutableList<Comentario> = mutableListOf(),
    val evidencias: MutableList<String> = mutableListOf()
)

enum class Prioridad {
    BAJA, MEDIA, ALTA, CRITICA
}

enum class Estado {
    ABIERTO, EN_PROGRESO, PENDIENTE, RESUELTO, CERRADO
}
