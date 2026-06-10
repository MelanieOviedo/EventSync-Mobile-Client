package com.moviles.eventsync.core.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    /**
     * Convierte una fecha tipo ISO "2026-07-15T20:00:00" 
     * a "Miércoles 15 de julio, 2026 8:00 pm"
     */
    fun formatEventDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Fecha no disponible"
        
        return try {
            // Formato de entrada (ISO 8601)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            
            // Formato de salida deseado: "EEEE dd 'de' MMMM, yyyy h:mm a"
            val outputFormat = SimpleDateFormat("EEEE dd 'de' MMMM, yyyy  h:mm a", Locale("es", "ES"))
            
            val formatted = outputFormat.format(date!!)
            // Capitalizar la primera letra (día de la semana)
            formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } catch (e: Exception) {
            // Si falla, devolvemos el original para no romper la UI
            dateString
        }
    }
}
