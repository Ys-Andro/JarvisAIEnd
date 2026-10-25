package com.example.jarvisai.data.ai

import com.example.jarvisai.domain.model.CloudAiModel
import com.example.jarvisai.domain.repository.ISettingsRepository
import com.example.jarvisai.domain.model.AppThemeMode
import com.example.jarvisai.domain.model.GenerationSettings
import com.example.jarvisai.domain.model.UserProfile
import com.example.jarvisai.domain.model.CloudAiModel as Model
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ModelRouterTest {

    private lateinit var fakeSettingsRepository: ISettingsRepository
    private lateinit var router: ModelRouter

    @Before
    fun setUp() {
        fakeSettingsRepository = object : ISettingsRepository {
            override fun getSettings(): Flow<GenerationSettings> = flowOf(GenerationSettings())
            override suspend fun updateSettings(settings: GenerationSettings) {}
            override fun getUserProfile(): Flow<UserProfile> = flowOf(UserProfile())
            override suspend fun updateUserProfile(profile: UserProfile) {}
            override fun getApiKey(): Flow<String?> = flowOf(null)
            override suspend fun updateApiKey(apiKey: String) {}
            override fun getProviderApiKey(providerId: String): Flow<String?> = flowOf(null)
            override suspend fun updateProviderApiKey(providerId: String, apiKey: String) {}
            override fun getAllProviderApiKeys(): Flow<Map<String, String>> = flowOf(emptyMap())
            override fun getCustomOpenAiEndpoint(): Flow<String?> = flowOf(null)
            override suspend fun updateCustomOpenAiEndpoint(endpoint: String) {}
            override fun getSelectedGeminiModel(): Flow<String> = flowOf("gemini-3.6-flash")
            override suspend fun updateSelectedGeminiModel(model: String) {}
            override fun getAppTheme(): Flow<AppThemeMode> = flowOf(AppThemeMode.DARK_JARVIS)
            override suspend fun setAppTheme(theme: AppThemeMode) {}
            override fun getSelectedAgentId(): Flow<String> = flowOf("jarvis")
            override suspend fun setSelectedAgentId(agentId: String) {}
            override fun getHotwordSensitivity(): Flow<String> = flowOf("medium")
            override suspend fun setHotwordSensitivity(level: String) {}
            override fun getHotwordAutoLaunchApp(): Flow<Boolean> = flowOf(true)
            override suspend fun setHotwordAutoLaunchApp(enable: Boolean) {}
        }
        router = ModelRouter(fakeSettingsRepository)
    }

    @Test
    fun `determineComplexity returns HIGH for long prompts`() {
        val longPrompt = "a".repeat(401)
        assertEquals(ModelRouter.Complexity.HIGH, router.determineComplexity(longPrompt))
    }

    @Test
    fun `determineComplexity returns HIGH for complex keywords`() {
        val prompts = listOf(
            "analiza este código",
            "explica detalladamente la relatividad",
            "escribe un algoritmo de ordenamiento",
            "planifica mi viaje"
        )
        for (p in prompts) {
            assertEquals("Failed for: $p", ModelRouter.Complexity.HIGH, router.determineComplexity(p))
        }
    }

    @Test
    fun `determineComplexity returns LOW for simple greetings`() {
        val prompts = listOf(
            "Hola Jarvis",
            "¿Qué hora es?",
            "¿Cómo estás?",
            "Luz por favor"
        )
        for (p in prompts) {
            assertEquals("Failed for: $p", ModelRouter.Complexity.LOW, router.determineComplexity(p))
        }
    }
}
