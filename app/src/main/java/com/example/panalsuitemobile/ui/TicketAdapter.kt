package com.example.panalsuitemobile.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panalsuitemobile.R
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
        val barra:       View     = itemView.findViewById(R.id.viewPrioridadBarra)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.tvTitulo.text    = ticket.titulo
        holder.tvCategoria.text = ticket.categoria
        holder.tvFecha.text     = ticket.fechaCreacion
        holder.tvEstado.chip(ticket.estado.name.replace("_", " "), ticket.estado.color())
        holder.tvPrioridad.chip(ticket.prioridad.name, ticket.prioridad.color())
        holder.barra.setBackgroundColor(ticket.prioridad.color())

        holder.itemView.setOnClickListener { onClick(ticket) }
    }

    override fun getItemCount() = tickets.size

    fun actualizar(nuevosTickets: List<Ticket>) {
        tickets = nuevosTickets
        notifyDataSetChanged()
    }
}
