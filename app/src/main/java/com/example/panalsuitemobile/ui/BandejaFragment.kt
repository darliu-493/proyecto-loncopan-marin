package com.example.panalsuitemobile.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.panalsuitemobile.R
import com.example.panalsuitemobile.model.Estado
import com.example.panalsuitemobile.viewmodel.BandejaViewModel
import com.example.panalsuitemobile.viewmodel.SessionManager

class BandejaFragment : Fragment(R.layout.fragment_bandeja) {

    private val viewModel: BandejaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvBienvenida = view.findViewById<TextView>(R.id.tvBienvenida)
        val tvRol = view.findViewById<TextView>(R.id.tvRol)
        val rvTickets = view.findViewById<RecyclerView>(R.id.rvTickets)
        val btnCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)
        val tvVacio = view.findViewById<TextView>(R.id.tvVacio)
        val tvResumen = view.findViewById<TextView>(R.id.tvResumen)
        iconosBarraOscuros(false)

        // Respetar barra de estado / navegación
        view.findViewById<View>(R.id.headerBandeja).aplicarInsets(arriba = true)
        rvTickets.aplicarInsets(abajo = true)

        // 1. Mostrar información del usuario
        val usuario = SessionManager.usuarioActual
        if (usuario != null) {
            tvBienvenida?.text = "Hola, ${usuario.nombre.substringBefore(" ")}"
            tvRol?.text = usuario.rol.name.lowercase().replaceFirstChar { it.uppercase() }
        }

        // 2. Configurar RecyclerView
        val adapter = TicketAdapter(emptyList()) { ticket ->
            // Navegar al detalle al hacer clic en un ticket
            val bundle = Bundle().apply { putInt("ticketId", ticket.id) }
            findNavController().navigate(R.id.action_bandeja_to_detalle, bundle)
        }
        rvTickets.layoutManager = LinearLayoutManager(requireContext())
        rvTickets.adapter = adapter

        // 3. Observar lista de tickets
        viewModel.tickets.observe(viewLifecycleOwner) { listaTickets ->
            adapter.actualizar(listaTickets)
            rvTickets.scheduleLayoutAnimation()
            tvVacio.visibility = if (listaTickets.isEmpty()) View.VISIBLE else View.GONE

            val enCurso = listaTickets.count {
                it.estado == Estado.ABIERTO || it.estado == Estado.EN_PROGRESO
            }
            tvResumen.text = when {
                listaTickets.isEmpty() -> ""
                listaTickets.size == 1 -> "1 ticket · $enCurso en curso"
                else -> "${listaTickets.size} tickets · $enCurso en curso"
            }
        }

        // 4. Cargar datos
        viewModel.cargarTickets()

        // 5. Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener {
            SessionManager.clearSession()
            findNavController().navigate(R.id.action_bandeja_to_login)
        }
    }
}
