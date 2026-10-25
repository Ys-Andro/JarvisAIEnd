package com.example.jarvisai.data.api.gemini

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GeminiJsonMapperTest {

    @Test
    fun `buildRequestBody includes thinkingLevel for gemini-3 models`() {
        val config = GeminiGenerationConfig(thinkingLevel = "high")
        val jsonString = GeminiJsonMapper.buildRequestBody(
            prompt = "Test prompt",
            history = emptyList(),
            systemPrompt = "Sys prompt",
            config = config,
            modelId = "gemini-3.6-flash"
        )
        
        val json = JSONObject(jsonString)
        val genConfig = json.getJSONObject("generationConfig")
        val thinkingConfig = genConfig.getJSONObject("thinkingConfig")
        
        assertEquals("high", thinkingConfig.getString("thinkingLevel"))
        assertFalse(genConfig.has("thinkingBudget"))
    }

    @Test
    fun `buildRequestBody includes thinkingBudget for gemini-2-5-flash`() {
        val config = GeminiGenerationConfig(thinkingBudget = 0)
        val jsonString = GeminiJsonMapper.buildRequestBody(
            prompt = "Test prompt",
            history = emptyList(),
            systemPrompt = "Sys prompt",
            config = config,
            modelId = "gemini-2.5-flash"
        )
        
        val json = JSONObject(jsonString)
        val genConfig = json.getJSONObject("generationConfig")
        
        assertEquals(0, genConfig.getInt("thinkingBudget"))
        assertFalse(genConfig.has("thinkingConfig"))
    }

    @Test
    fun `buildRequestBody avoids thinkingLevel for other models`() {
        val config = GeminiGenerationConfig(thinkingLevel = "low")
        val jsonString = GeminiJsonMapper.buildRequestBody(
            prompt = "Test prompt",
            history = emptyList(),
            systemPrompt = "Sys prompt",
            config = config,
            modelId = "gemini-1.5-pro"
        )
        
        val json = JSONObject(jsonString)
        val genConfig = json.getJSONObject("generationConfig")
        
        assertFalse(genConfig.has("thinkingConfig"))
        assertFalse(genConfig.has("thinkingBudget"))
    }

    @Test
    fun `parseStreamChunk extracts text correctly`() {
        val chunk = """
            {
                "candidates": [{
                    "content": {
                        "parts": [{"text": "Hello "}, {"text": "world"}]
                    }
                }]
            }
        """.trimIndent()
        
        val result = GeminiJsonMapper.parseStreamChunk(chunk)
        assertEquals("Hello world", result)
    }
}
