package com.example.panalsuitemobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.panalsuitemobile.model.Rol
import com.example.panalsuitemobile.model.Ticket
import com.example.panalsuitemobile.repository.Repository

class BandejaViewModel : ViewModel() {

    private val _tickets = MutableLiveData<List<Ticket>>()
    val tickets: LiveData<List<Ticket>> = _tickets

    fun cargarTickets() {
        val usuario = SessionManager.usuarioActual ?: return
        val lista = when (usuario.rol) {
            Rol.SOLICITANTE  -> Repository.getTicketsPorSolicitante(usuario.nombre)
            Rol.RESOLUTOR    -> Repository.getTicketsPorResolutor(usuario.nombre)
            Rol.SUPERVISOR, Rol.GERENCIA, Rol.ADMINISTRADOR -> Repository.getTickets()
        }
        _tickets.value = lista
    }
}
