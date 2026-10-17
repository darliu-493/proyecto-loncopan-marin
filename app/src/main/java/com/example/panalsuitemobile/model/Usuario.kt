package com.example.panalsuitemobile.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: Rol
)

enum class Rol {
    SOLICITANTE,
    RESOLUTOR,
    SUPERVISOR,
    GERENCIA,
    ADMINISTRADOR
}
