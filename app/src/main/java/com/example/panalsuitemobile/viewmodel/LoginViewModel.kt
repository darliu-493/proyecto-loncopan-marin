package com.example.panalsuitemobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.panalsuitemobile.model.Usuario
import com.example.panalsuitemobile.repository.Repository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginResultado = MutableLiveData<Usuario?>()
    val loginResultado: LiveData<Usuario?> get() = _loginResultado

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun login(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _error.value = "Por favor completa todos los campos"
            return
        }

        val usuario = Repository.login(email, pass)
        if (usuario != null) {
            SessionManager.usuarioActual = usuario
            _error.value = null
            _loginResultado.value = usuario
        } else {
            _error.value = "Credenciales incorrectas"
        }
    }
}