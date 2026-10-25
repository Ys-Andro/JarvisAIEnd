package com.example.jarvisai.data.api.gemini

import org.json.JSONArray
import org.json.JSONObject

data class GeminiMessage(
    val role: String, // "user" or "model"
    val text: String
)

data class GeminiInlineData(
    val mimeType: String,
    val base64Data: String
)

data class GeminiGenerationConfig(
    val temperature: Float = 0.7f,
    val topP: Float = 0.95f,
    val topK: Int = 40,
    val maxOutputTokens: Int = 2048,
    val thinkingLevel: String? = null,
    val thinkingBudget: Int? = null
)

object GeminiJsonMapper {

    fun buildRequestBody(
        prompt: String,
        history: List<GeminiMessage>,
        systemPrompt: String?,
        config: GeminiGenerationConfig,
        inlineData: GeminiInlineData? = null,
        modelId: String? = null
    ): String {
        val root = JSONObject()

        // 1. System instruction (if present)
        if (!systemPrompt.isNullOrBlank()) {
            val sysPart = JSONObject().put("text", systemPrompt)
            val sysContent = JSONObject()
                .put("parts", JSONArray().put(sysPart))
            root.put("systemInstruction", sysContent)
        }

        // 2. Contents array (history + current prompt)
        val contentsArray = JSONArray()
        for (msg in history) {
            val part = JSONObject().put("text", msg.text)
            val contentObj = JSONObject()
                .put("role", if (msg.role == "assistant" || msg.role == "model") "model" else "user")
                .put("parts", JSONArray().put(part))
            contentsArray.put(contentObj)
        }

        // Current user prompt with optional inline image
        val currentParts = JSONArray()
        if (inlineData != null) {
            val imagePart = JSONObject().put("inlineData", JSONObject().apply {
                put("mimeType", inlineData.mimeType)
                put("data", inlineData.base64Data)
            })
            currentParts.put(imagePart)
        }
        val textPart = JSONObject().put("text", prompt)
        currentParts.put(textPart)

        val currentContent = JSONObject()
            .put("role", "user")
            .put("parts", currentParts)
        contentsArray.put(currentContent)

        root.put("contents", contentsArray)

        // 3. Generation config
        val genConfig = JSONObject()
            .put("temperature", config.temperature)
            .put("topP", config.topP)
            .put("topK", config.topK)
            .put("maxOutputTokens", config.maxOutputTokens)

        // Thinking configuration
        if (modelId?.startsWith("gemini-3") == true && config.thinkingLevel != null) {
            val thinkingConfig = JSONObject().put("thinkingLevel", config.thinkingLevel)
            genConfig.put("thinkingConfig", thinkingConfig)
        } else if (modelId == "gemini-2.5-flash") {
            // Map thinkingLevel to thinkingBudget if present, otherwise use explicit budget
            val budget = when {
                config.thinkingLevel == "low" -> 0
                config.thinkingLevel == "high" -> 32000 // Sample high budget
                else -> config.thinkingBudget
            }
            if (budget != null) {
                genConfig.put("thinkingBudget", budget)
            }
        }

        root.put("generationConfig", genConfig)

        return root.toString()
    }

    fun parseStreamChunk(chunkJson: String): String {
        return try {
            val json = JSONObject(chunkJson)
            val candidates = json.optJSONArray("candidates") ?: return ""
            if (candidates.length() == 0) return ""
            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return ""
            val parts = content.optJSONArray("parts") ?: return ""
            val sb = java.lang.StringBuilder()
            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                val text = part.optString("text", "")
                sb.append(text)
            }
            sb.toString()
        } catch (_: Exception) {
            ""
        }
    }
}
