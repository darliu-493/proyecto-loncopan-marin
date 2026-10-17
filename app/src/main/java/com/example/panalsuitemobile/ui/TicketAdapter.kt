package com.example.panalsuitemobile.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panalsuitemobile.R
import com.example.panalsuitemobile.model.Prioridad
import com.example.panalsuitemobile.model.Ticket

class TicketAdapter(
    private var tickets: List<Ticket>,
    private val onClick: (Ticket) -> Unit
) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo:    TextView = itemView.findViewById(R.id.tvTitulo)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val tvEstado:    TextView = itemView.findViewById(R.id.tvEstado)
        val tvFecha:     TextView = itemView.findViewById(R.id.tvFecha)
        val tvPrioridad: TextView = itemView.findViewById(R.id.tvPrioridad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.tvTitulo.text    = ticket.titulo
        holder.tvCategoria.text = " ${ticket.categoria}"
        holder.tvEstado.text    = " ${ticket.estado.name.replace("_", " ")}"
        holder.tvFecha.text     = ticket.fechaCreacion
        holder.tvPrioridad.text = ticket.prioridad.name

        val color = when (ticket.prioridad) {
            Prioridad.BAJA    -> Color.parseColor("#43A047")
            Prioridad.MEDIA   -> Color.parseColor("#FB8C00")
            Prioridad.ALTA    -> Color.parseColor("#E53935")
            Prioridad.CRITICA -> Color.parseColor("#6A1B9A")
        }
        holder.tvPrioridad.setBackgroundColor(color)

        holder.itemView.setOnClickListener { onClick(ticket) }
    }

    override fun getItemCount() = tickets.size

    fun actualizar(nuevosTickets: List<Ticket>) {
        tickets = nuevosTickets
        notifyDataSetChanged()
    }
}
