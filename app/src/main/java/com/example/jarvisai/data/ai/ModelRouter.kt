package com.example.jarvisai.data.ai

import com.example.jarvisai.domain.model.CloudAiModel
import com.example.jarvisai.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.first

/**
 * Deterministic model routing policy. The selected model is always tried first.
 * Fallbacks are considered only when the primary fails before producing output.
 */
class ModelRouter(
    private val settingsRepository: ISettingsRepository
) {
    enum class Complexity { LOW, HIGH }

    fun determineComplexity(prompt: String): Complexity {
        val normalized = prompt.trim().lowercase()
        if (normalized.length > 400) return Complexity.HIGH
        
        val complexSignals = listOf(
            "analiza", "planifica", "resume", "compara", "explica detalladamente",
            "paso a paso", "crítica", "investiga", "escribe un", "código", "programación",
            "algoritmo", "matemática", "lógica"
        )
        if (complexSignals.any { normalized.contains(it) }) return Complexity.HIGH
        
        return Complexity.LOW
    }

    suspend fun candidates(primaryId: String, complexity: Complexity = Complexity.LOW): List<CloudAiModel> {
        val configured = settingsRepository.getAllProviderApiKeys().first()
        val result = mutableListOf<CloudAiModel>()

        // 1. Level Selection
        val primary = CloudAiModel.findById(primaryId)
        result.add(primary)

        fun add(id: String) {
            if (id == "openrouter/auto" || id == "local-qwen-hf") return
            val model = CloudAiModel.ALL_MODELS.firstOrNull { it.id == id } ?: return
            if (result.none { it.id == model.id }) {
                // For fallback, we only care if we have some API key, or if it's the default Gemini which might use BuildConfig
                val providerId = model.provider.id.lowercase()
                val hasKey = configured[providerId].orEmpty().isNotBlank()
                if (hasKey || model.provider == com.example.jarvisai.domain.model.ModelProvider.GEMINI) {
                    result += model
                }
            }
        }

        // Add logical next candidates based on complexity
        if (complexity == Complexity.HIGH) {
            add("gemini-3.6-flash") 
            add("deepseek-reasoner")
            add("claude-3-5-sonnet-20241022")
        } else {
            add("gemini-3.6-flash")
            add("gpt-4o-mini")
        }

        // Final Fallback: Groq or Custom OpenAI
        add("openai/gpt-oss-20b")
        add("openai/gpt-oss-120b")
        add("custom")

        return result.distinctBy { it.id }.take(3)
    }
}
