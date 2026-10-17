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

        // ── CONTROL DE ROLES ──────────────────────────────
        val rol = SessionManager.usuarioActual?.rol
        when (rol) {
            Rol.SOLICITANTE, Rol.GERENCIA -> {
                // Solo pueden ver, no modificar
                cardCambiarEstado.visibility = View.GONE
                btnComentario.visibility     = View.GONE
                etComentario.visibility      = View.GONE
                btnFoto.visibility           = View.GONE
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
            tvCategoria.text   = " Categoría: ${ticket.categoria}"
            tvPrioridad.text   = " Prioridad: ${ticket.prioridad.name}"
            tvEstado.text      = " Estado: ${ticket.estado.name.replace("_", " ")}"
            tvSolicitante.text = " Solicitante: ${ticket.solicitante}"
            tvResolutor.text   = " Resolutor: ${ticket.resolutor}"
            tvFecha.text       = " Creado: ${ticket.fechaCreacion} | Act: ${ticket.fechaActualizacion}"

            val estadoIndex = Estado.values().indexOf(ticket.estado)
            if (estadoIndex >= 0) spinnerEstado.setSelection(estadoIndex)

            // ── MOSTRAR COMENTARIOS ───────────────────────
            llComentarios.removeAllViews()
            if (ticket.comentarios.isEmpty()) {
                val tvSinComentarios = TextView(requireContext()).apply {
                    text = "Sin comentarios aún"
                    setTextColor(android.graphics.Color.parseColor("#AAAAAA"))
                    textSize = 13f
                    setPadding(0, 8, 0, 8)
                }
                llComentarios.addView(tvSinComentarios)
            } else {
                ticket.comentarios.forEach { comentario ->
                    val tvComentario = TextView(requireContext()).apply {
                        text = "💬 ${comentario.autor} (${comentario.fecha}):\n${comentario.texto}"
                        textSize = 13f
                        setTextColor(android.graphics.Color.parseColor("#333333"))
                        setBackgroundColor(android.graphics.Color.parseColor("#F0F0F0"))
                        setPadding(16, 12, 16, 12)
                    }
                    llComentarios.addView(tvComentario)

                    // Separador
                    val separador = View(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1
                        ).also { it.setMargins(0, 4, 0, 4) }
                        setBackgroundColor(android.graphics.Color.parseColor("#DDDDDD"))
                    }
                    llComentarios.addView(separador)
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
