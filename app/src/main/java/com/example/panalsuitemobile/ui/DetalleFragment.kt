package com.example.panalsuitemobile.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.panalsuitemobile.R
import com.example.panalsuitemobile.model.Estado
import com.example.panalsuitemobile.model.Rol
import com.example.panalsuitemobile.viewmodel.DetalleViewModel
import com.example.panalsuitemobile.viewmodel.SessionManager

class DetalleFragment : Fragment() {

    private lateinit var viewModel: DetalleViewModel
    private var ticketId: Int = -1
    private val REQUEST_IMAGE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_detalle, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DetalleViewModel::class.java]
        ticketId  = arguments?.getInt("ticketId") ?: -1

        val tvTitulo          = view.findViewById<TextView>(R.id.tvDetalleTitulo)
        val tvDescripcion     = view.findViewById<TextView>(R.id.tvDetalleDescripcion)
        val tvCategoria       = view.findViewById<TextView>(R.id.tvDetalleCategoria)
        val tvPrioridad       = view.findViewById<TextView>(R.id.tvDetallePrioridad)
        val tvEstado          = view.findViewById<TextView>(R.id.tvDetalleEstado)
        val tvSolicitante     = view.findViewById<TextView>(R.id.tvDetalleSolicitante)
        val tvResolutor       = view.findViewById<TextView>(R.id.tvDetalleResolutor)
        val tvFecha           = view.findViewById<TextView>(R.id.tvDetalleFecha)
        val spinnerEstado     = view.findViewById<Spinner>(R.id.spinnerEstado)
        val btnActualizar     = view.findViewById<Button>(R.id.btnActualizarEstado)
        val cardCambiarEstado = view.findViewById<View>(R.id.cardCambiarEstado)
        val etComentario      = view.findViewById<EditText>(R.id.etComentario)
        val btnComentario     = view.findViewById<Button>(R.id.btnAgregarComentario)
        val llComentarios     = view.findViewById<LinearLayout>(R.id.llComentarios)
        val btnFoto           = view.findViewById<Button>(R.id.btnTomarFoto)
        val ivEvidencia       = view.findViewById<ImageView>(R.id.ivEvidencia)
        val layoutComentario  = view.findViewById<View>(R.id.layoutComentario)
        val cardEvidencia     = view.findViewById<View>(R.id.cardEvidencia)

        iconosBarraOscuros(false)
        view.findViewById<View>(R.id.headerDetalle).aplicarInsets(arriba = true)
        view.findViewById<View>(R.id.scrollDetalle).aplicarInsets(abajo = true)
        view.findViewById<View>(R.id.btnVolver).setOnClickListener { findNavController().navigateUp() }

        // ── CONTROL DE ROLES ──────────────────────────────
        val rol = SessionManager.usuarioActual?.rol
        when (rol) {
            Rol.SOLICITANTE, Rol.GERENCIA -> {
                // Solo pueden ver, no modificar
                cardCambiarEstado.visibility = View.GONE
                btnComentario.visibility     = View.GONE
                etComentario.visibility      = View.GONE
                layoutComentario.visibility  = View.GONE
                btnFoto.visibility           = View.GONE
                cardEvidencia.visibility     = View.GONE
            }
            Rol.RESOLUTOR -> {
                // Puede comentar y agregar evidencia pero no cambiar estado de otros
                cardCambiarEstado.visibility = View.VISIBLE
                btnComentario.visibility     = View.VISIBLE
                etComentario.visibility      = View.VISIBLE
                btnFoto.visibility           = View.VISIBLE
            }
            Rol.SUPERVISOR, Rol.ADMINISTRADOR -> {
                // Acceso completo
                cardCambiarEstado.visibility = View.VISIBLE
                btnComentario.visibility     = View.VISIBLE
                etComentario.visibility      = View.VISIBLE
                btnFoto.visibility           = View.VISIBLE
            }
            else -> {}
        }

        // ── SPINNER DE ESTADOS ────────────────────────────
        val estados = Estado.values().map { it.name.replace("_", " ") }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = spinnerAdapter

        // ── OBSERVAR TICKET ───────────────────────────────
        viewModel.ticket.observe(viewLifecycleOwner) { ticket ->
            ticket ?: return@observe

            tvTitulo.text      = ticket.titulo
            tvDescripcion.text = ticket.descripcion
            tvCategoria.text   = ticket.categoria
            tvPrioridad.chip(ticket.prioridad.name, ticket.prioridad.color())
            tvEstado.chip(ticket.estado.name.replace("_", " "), ticket.estado.color())
            tvSolicitante.text = ticket.solicitante
            tvResolutor.text   = ticket.resolutor
            tvFecha.text       = "Creado: ${ticket.fechaCreacion}  ·  Actualizado: ${ticket.fechaActualizacion}"

            val estadoIndex = Estado.values().indexOf(ticket.estado)
            if (estadoIndex >= 0) spinnerEstado.setSelection(estadoIndex)

            // ── MOSTRAR COMENTARIOS ───────────────────────
            llComentarios.removeAllViews()
            if (ticket.comentarios.isEmpty()) {
                val tvSinComentarios = TextView(requireContext()).apply {
                    text = "Sin comentarios aún"
                    setTextColor(android.graphics.Color.parseColor("#9CA3AF"))
                    textSize = 13f
                    setPadding(0, 16, 0, 8)
                }
                llComentarios.addView(tvSinComentarios)
            } else {
                ticket.comentarios.forEach { comentario ->
                    val item = layoutInflater.inflate(R.layout.item_comentario, llComentarios, false)
                    item.findViewById<TextView>(R.id.tvComAutor).text = comentario.autor
                    item.findViewById<TextView>(R.id.tvComFecha).text = comentario.fecha
                    item.findViewById<TextView>(R.id.tvComTexto).text = comentario.texto
                    llComentarios.addView(item)
                }
            }
        }

        // ── OBSERVAR MENSAJES ─────────────────────────────
        viewModel.mensaje.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.cargarTicket(ticketId)

            // ── ACTUALIZAR ESTADO ─────────────────────────────
            btnActualizar.setOnClickListener {
            val selectedEstado = Estado.values()[spinnerEstado.selectedItemPosition]
            viewModel.actualizarEstado(ticketId, selectedEstado)
        }

        // ── AGREGAR COMENTARIO ────────────────────────────
        btnComentario.setOnClickListener {
            val texto = etComentario.text.toString()
            if (texto.isBlank()) {
                etComentario.error = "Escribe algo antes de agregar"
                return@setOnClickListener
            }
            viewModel.agregarComentario(ticketId, texto)
            etComentario.text.clear()
        }

        // ── TOMAR FOTO ────────────────────────────────────
        btnFoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            val foto = data?.extras?.get("data") as? android.graphics.Bitmap
            val ivEvidencia = view?.findViewById<ImageView>(R.id.ivEvidencia)
            ivEvidencia?.setImageBitmap(foto)
            ivEvidencia?.visibility = View.VISIBLE
            viewModel.agregarEvidencia(ticketId, "foto_capturada")
        }
    }
}
