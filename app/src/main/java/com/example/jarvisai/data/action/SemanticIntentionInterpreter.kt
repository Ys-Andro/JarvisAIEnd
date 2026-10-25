package com.example.jarvisai.data.action

import java.util.Locale

/**
 * Local Semantic Interpreter for Jarvis AI.
 * Handles natural language instructions for simple device actions without needing a heavy AI model.
 * Supports synonyms, context-aware expressions, and common spelling mistakes.
 */
class SemanticIntentionInterpreter {
    
    fun interpretLocal(input: String): String? {
        val normalized = input.lowercase(Locale.getDefault()).trim()
        if (normalized.length < 3) return null

        // 1. Flashlight (Linterna)
        if (containsAny(normalized, "oscuro", "luz", "linterna", "prende", "encender", "ilumina", "flash", "antorcha", "no veo", "brilla")) {
            if (containsAny(normalized, "apaga", "quita", "desactiva", "fuera", "stop", "detén")) {
                return """{"action":"FLASHLIGHT", "enable": false}"""
            }
            if (containsAny(normalized, "enciende", "prende", "activa", "pon", "ilumina", "oscuro", "linterna")) {
                return """{"action":"FLASHLIGHT", "enable": true}"""
            }
        }
        
        // 2. Battery (Batería)
        if (containsAny(normalized, "batería", "energia", "energía", "pila", "carga", "cuanto queda", "cuánta queda", "porcentaje", "me queda")) {
             if (containsAny(normalized, "batería", "energia", "energía", "pila", "carga", "me queda")) {
                 return """{"action":"BATTERY_STATUS"}"""
             }
        }

        // 3. Volume/Mute (Silencio)
        if (containsAny(normalized, "silencio", "no quiero escuchar", "calla", "mudo", "shh", "silenciar", "quita el sonido", "volumen a cero")) {
            return """{"action":"MUTE"}"""
        }
        
        // 4. Time/Date
        if (containsAny(normalized, "qué hora", "que hora", "qué día", "que dia", "fecha hoy", "en qué día", "en que dia")) {
            return """{"action":"GET_CURRENT_DATETIME"}"""
        }
        
        // 5. Timer/Alarm (Shortcuts)
        if (containsAny(normalized, "avísame", "avisame", "recuérdame", "recuerdame", "pon un temporizador", "cuenta atrás", "cuenta atras")) {
             if (normalized.contains("diez minutos") || normalized.contains("10 min")) {
                 return """{"action":"SET_TIMER", "seconds": 600, "message": "Temporizador de 10 minutos finalizado"}"""
             }
             if (normalized.contains("cinco minutos") || normalized.contains("5 min")) {
                 return """{"action":"SET_TIMER", "seconds": 300, "message": "Temporizador de 5 minutos finalizado"}"""
             }
             if (normalized.contains("un minuto") || normalized.contains("1 min")) {
                 return """{"action":"SET_TIMER", "seconds": 60, "message": "Temporizador de 1 minuto finalizado"}"""
             }
        }
        
        // 6. Navigation/Home
        if (containsAny(normalized, "ir a casa", "pantalla de inicio", "vuelve al inicio", "sal de aquí", "quitar todo")) {
            return """{"action":"HOME"}"""
        }

        // 7. Open Music
        if (containsAny(normalized, "ponme la aplicación de música", "pon musica", "pon música", "escuchar algo", "reproduce algo")) {
            return """{"action":"OPEN_APP", "appName": "Spotify"}"""
        }

        return null
    }

    private fun containsAny(input: String, vararg keywords: String): Boolean {
        return keywords.any { input.contains(it) }
    }
}
