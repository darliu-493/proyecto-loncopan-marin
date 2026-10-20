package com.example.panalsuitemobile.ui

import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import android.graphics.Color
import android.widget.TextView
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.panalsuitemobile.model.Estado
import com.example.panalsuitemobile.model.Prioridad

fun Prioridad.color(): Int = when (this) {
    Prioridad.BAJA    -> Color.parseColor("#16A34A")
    Prioridad.MEDIA   -> Color.parseColor("#F59E0B")
    Prioridad.ALTA    -> Color.parseColor("#EF4444")
    Prioridad.CRITICA -> Color.parseColor("#7C3AED")
}

fun Estado.color(): Int = when (this) {
    Estado.ABIERTO     -> Color.parseColor("#3B82F6")
    Estado.EN_PROGRESO -> Color.parseColor("#F59E0B")
    Estado.PENDIENTE   -> Color.parseColor("#6B7280")
    Estado.RESUELTO    -> Color.parseColor("#16A34A")
    Estado.CERRADO     -> Color.parseColor("#334155")
}

/** Escribe el texto y lo pinta del color indicado (sin chips ni fondos). */
fun TextView.chip(texto: String, color: Int) {
    text = texto
    setTextColor(color)
}

/** Iconos de la barra de estado: oscuros sobre fondo claro, blancos sobre la barra verde. */
fun Fragment.iconosBarraOscuros(oscuros: Boolean) {
    val w = requireActivity().window
    WindowCompat.getInsetsController(w, w.decorView).isAppearanceLightStatusBars = oscuros
}

/** Suma los insets de la barra de estado / navegación / teclado al padding actual. */
fun View.aplicarInsets(arriba: Boolean = false, abajo: Boolean = false) {
    val pl = paddingLeft
    val pt = paddingTop
    val pr = paddingRight
    val pb = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val b = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        )
        v.setPadding(pl, pt + if (arriba) b.top else 0, pr, pb + if (abajo) b.bottom else 0)
        insets
    }
    ViewCompat.requestApplyInsets(this)
}
