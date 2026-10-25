package com.example.jarvisai.data.action

object ActionRegistry {
    val SUPPORTED_ACTIONS = setOf(
        "FLASHLIGHT", "VOLUME", "MUTE", "BATTERY", "BATTERY_STATUS", "GET_CURRENT_DATETIME",
        "SET_ALARM", "SET_TIMER", "OPEN_APP", "WHATSAPP_MESSAGE",
        "YOUTUBE_SEARCH", "YOUTUBE", "MAPS_NAVIGATE", "NAVIGATE",
        "MAPS_SEARCH", "SPOTIFY", "SPOTIFY_PLAY", "CALL",
        "WEB_SEARCH", "OPEN_URL", "GET_NOTIFICATIONS", "READ_NOTIFICATIONS",
        "HOME", "BACK", "RECENTS", "NOTIFICATIONS", "QUICK_SETTINGS",
        "LOCK_SCREEN", "SCREENSHOT", "SCROLL_DOWN", "SCROLL_UP",
        "CLICK_TEXT", "TYPE_TEXT", "READ_SCREEN", "OPEN_SETTINGS",
        "OPEN_WIFI", "OPEN_BLUETOOTH", "VIBRATE", "SHOW_REMINDER", "SCHEDULE_ACTION", "CANCEL_AUTOMATION", "SAVE_MEMORY", "DELETE_MEMORY"
    )

    val SENSITIVE_ACTIONS = setOf(
        "CALL",
        "WHATSAPP_MESSAGE",
        "TYPE_TEXT",
        "CLICK_TEXT",
        "DELETE_MEMORY",
        "SCREENSHOT",
        "READ_NOTIFICATIONS",
        "READ_SCREEN",
        "OPEN_URL",
        "SCHEDULE_ACTION",
        "CANCEL_AUTOMATION",
        "SAVE_MEMORY"
    )

    fun canonicalName(action: String): String {
        val upper = action.trim().uppercase()
        return when (upper) {
            "GET_NOTIFICATIONS" -> "READ_NOTIFICATIONS"
            "BATTERY" -> "BATTERY_STATUS"
            "ENERGIA", "ENERGÍA", "PILA" -> "BATTERY_STATUS"
            "LUZ", "LINTERNA" -> "FLASHLIGHT"
            "SILENCIO", "MUTEAR" -> "MUTE"
            "NAVIGATE", "IR_A" -> "MAPS_NAVIGATE"
            "SEARCH" -> "WEB_SEARCH"
            "REPRODUCIR", "PLAY" -> "SPOTIFY_PLAY"
            else -> upper
        }
    }

    fun isSensitive(action: String): Boolean {
        val canonical = canonicalName(action)
        return SENSITIVE_ACTIONS.contains(canonical)
    }
}
