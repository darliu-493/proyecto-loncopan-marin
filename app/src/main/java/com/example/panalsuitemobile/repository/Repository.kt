package com.example.panalsuitemobile.repository

import com.example.panalsuitemobile.model.Comentario
import com.example.panalsuitemobile.model.Estado
import com.example.panalsuitemobile.model.Prioridad
import com.example.panalsuitemobile.model.Rol
import com.example.panalsuitemobile.model.Ticket
import com.example.panalsuitemobile.model.Usuario

object Repository {

    val usuarios = listOf(
        Usuario(1, "Darliette Loncopan", "darliette@gmail.com",  "1234", Rol.SOLICITANTE),
        Usuario(2, "Thomas Marín",       "thomas@gmail.com",     "1234", Rol.RESOLUTOR),
        Usuario(3, "Luna Moya",          "luna@gmail.com",       "1234", Rol.SUPERVISOR),
        Usuario(4, "Alexander Munoz",    "alexander@gmail.com",  "1234", Rol.GERENCIA),
        Usuario(5, "Alonso Maturana",    "alonso@gmail.com",     "1234", Rol.ADMINISTRADOR)
    )

    private val tickets = mutableListOf(
        Ticket(
            id = 1,
            titulo = "Error en sistema de cobros",
            descripcion = "El módulo de cobros no procesa pagos correctamente.",
            categoria = "Soporte TI",
            prioridad = Prioridad.ALTA,
            estado = Estado.ABIERTO,
            solicitante = "Darliette Loncopan",
            resolutor = "Thomas Marín",
            fechaCreacion = "2026-08-01",
            fechaActualizacion = "2026-08-02",
            comentarios = mutableListOf(
                Comentario(id = 1, autor = "Thomas Marín", texto = "Revisando el problema.", fecha = "2026-08-02")
            )
        ),
        Ticket(
            id = 2,
            titulo = "Actualizar datos de proveedor",
            descripcion = "Se requiere actualizar RUT y contacto del proveedor X.",
            categoria = "Administrativo",
            prioridad = Prioridad.MEDIA,
            estado = Estado.EN_PROGRESO,
            solicitante = "Alexander Munoz",
            resolutor = "David Gutierrez",
            fechaCreacion = "2026-08-03",
            fechaActualizacion = "2026-08-04"
        ),
        Ticket(
            id = 3,
            titulo = "Falla en impresora sucursal norte",
            descripcion = "La impresora no conecta a la red local.",
            categoria = "Infraestructura",
            prioridad = Prioridad.BAJA,
            estado = Estado.PENDIENTE,
            solicitante = "Luna Moya",
            resolutor = "Amanda Gutierrez",
            fechaCreacion = "2026-08-05",
            fechaActualizacion = "2026-08-05"
        ),
        Ticket(
            id = 4,
            titulo = "Solicitud de acceso al sistema",
            descripcion = "Nuevo empleado necesita acceso a PanalSuite.",
            categoria = "Accesos",
            prioridad = Prioridad.MEDIA,
            estado = Estado.ABIERTO,
            solicitante = "Pedro Soto",
            resolutor = "Alonso Maturana",
            fechaCreacion = "2026-08-06",
            fechaActualizacion = "2026-08-06"
        ),
        Ticket(
            id = 5,
            titulo = "Error en reporte mensual",
            descripcion = "El reporte de agosto no muestra datos correctos.",
            categoria = "Reportes",
            prioridad = Prioridad.CRITICA,
            estado = Estado.EN_PROGRESO,
            solicitante = "Jose Oses",
            resolutor = "Cristobal Maturana",
            fechaCreacion = "2026-08-07",
            fechaActualizacion = "2026-08-08",
            comentarios = mutableListOf(
                Comentario(id = 2, autor = "Alexander Muñoz", texto = "Urgente, afecta cierre de mes.", fecha = "2026-08-08")
            )
        )
    )

    fun getTickets(): List<Ticket> = tickets
    fun getTicketById(id: Int): Ticket? = tickets.find { it.id == id }
    fun getTicketsPorResolutor(nombre: String): List<Ticket> = tickets.filter { it.resolutor == nombre }
    fun getTicketsPorSolicitante(nombre: String): List<Ticket> = tickets.filter { it.solicitante == nombre }

    fun actualizarEstado(ticketId: Int, nuevoEstado: Estado) {
        val ticket = tickets.find { it.id == ticketId }
        val index = tickets.indexOf(ticket)
        if (index != -1 && ticket != null) {
            tickets[index] = ticket.copy(estado = nuevoEstado, fechaActualizacion = "2026-09-01")
        }
    }

    fun agregarComentario(ticketId: Int, comentario: Comentario) {
        tickets.find { it.id == ticketId }?.comentarios?.add(comentario)
    }

    fun agregarEvidencia(ticketId: Int, rutaFoto: String) {
        tickets.find { it.id == ticketId }?.evidencias?.add(rutaFoto)
    }

    fun login(email: String, password: String): Usuario? =
        usuarios.find { it.email == email && it.password == password }
}
