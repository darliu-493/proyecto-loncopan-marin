package com.example.panalsuitemobile.viewmodel

import com.example.panalsuitemobile.model.Usuario

object SessionManager {
    var usuarioActual: Usuario? = null

    fun clearSession() {
        usuarioActual = null
    }
}