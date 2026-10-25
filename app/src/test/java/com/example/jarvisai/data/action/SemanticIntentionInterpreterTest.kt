package com.example.jarvisai.data.action

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SemanticIntentionInterpreterTest {

    private lateinit var interpreter: SemanticIntentionInterpreter

    @Before
    fun setUp() {
        interpreter = SemanticIntentionInterpreter()
    }

    @Test
    fun `interpret flashlight on`() {
        val inputs = listOf(
            "enciende la linterna",
            "prende la luz",
            "está oscuro aquí",
            "ilumina por favor",
            "activa el flash"
        )
        for (input in inputs) {
            val result = interpreter.interpretLocal(input)
            assertNotNull("Failed for input: $input", result)
            assertTrue(result!!.contains("FLASHLIGHT"))
            assertTrue(result.contains("true"))
        }
    }

    @Test
    fun `interpret flashlight off`() {
        val inputs = listOf(
            "apaga la linterna",
            "quita la luz",
            "desactiva el flash",
            "stop linterna"
        )
        for (input in inputs) {
            val result = interpreter.interpretLocal(input)
            assertNotNull(result)
            assertTrue(result!!.contains("FLASHLIGHT"))
            assertTrue(result.contains("false"))
        }
    }

    @Test
    fun `interpret battery status`() {
        val inputs = listOf(
            "cuánta energía me queda",
            "estado de la batería",
            "pila actual",
            "porcentaje de carga"
        )
        for (input in inputs) {
            val result = interpreter.interpretLocal(input)
            assertNotNull(result)
            assertTrue(result!!.contains("BATTERY_STATUS"))
        }
    }

    @Test
    fun `interpret mute`() {
        val inputs = listOf(
            "silencio",
            "no quiero escuchar nada ahora",
            "pon el modo mudo",
            "calla a jarvis",
            "quita el sonido"
        )
        for (input in inputs) {
            val result = interpreter.interpretLocal(input)
            assertNotNull(result)
            assertTrue(result!!.contains("MUTE"))
        }
    }

    @Test
    fun `interpret timer`() {
        val input = "avísame dentro de diez minutos"
        val result = interpreter.interpretLocal(input)
        assertNotNull(result)
        assertTrue(result!!.contains("SET_TIMER"))
        assertTrue(result.contains("600"))
    }

    @Test
    fun `interpret open music`() {
        val input = "ponme la aplicación de música"
        val result = interpreter.interpretLocal(input)
        assertNotNull(result)
        assertTrue(result!!.contains("OPEN_APP"))
        assertTrue(result.contains("Spotify"))
    }

    @Test
    fun `return null for non-command`() {
        val input = "hola cómo estás hoy"
        val result = interpreter.interpretLocal(input)
        assertNull(result)
    }
}
