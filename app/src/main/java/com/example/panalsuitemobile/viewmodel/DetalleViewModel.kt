package com.example.panalsuitemobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.panalsuitemobile.model.Comentario
import com.example.panalsuitemobile.model.Estado
import com.example.panalsuitemobile.model.Ticket
import com.example.panalsuitemobile.repository.Repository

class DetalleViewModel : ViewModel() {

    private val _ticket = MutableLiveData<Ticket?>()
    val ticket: LiveData<Ticket?> = _ticket

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun cargarTicket(id: Int) {
        _ticket.value = Repository.getTicketById(id)
    }

    fun actualizarEstado(ticketId: Int, nuevoEstado: Estado) {
        Repository.actualizarEstado(ticketId, nuevoEstado)
        _ticket.value = Repository.getTicketById(ticketId)
        _mensaje.value = "Estado actualizado correctamente"
    }

    fun agregarComentario(ticketId: Int, texto: String) {
        if (texto.isBlank()) {
            _mensaje.value = "El comentario no puede estar vacío"
            return
        }
        val autor = SessionManager.usuarioActual?.nombre ?: "Desconocido"
        val nuevoComentario = Comentario(
            id = System.currentTimeMillis().toInt(),
            autor = autor,
            texto = texto,
            fecha = "2026-09-01"
        )
        Repository.agregarComentario(ticketId, nuevoComentario)
        _ticket.value = Repository.getTicketById(ticketId)
        _mensaje.value = "Comentario agregado"
    }

    fun agregarEvidencia(ticketId: Int, ruta: String) {
        Repository.agregarEvidencia(ticketId, ruta)
        _mensaje.value = "Evidencia guardada"
    }
}
