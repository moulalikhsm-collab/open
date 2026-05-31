package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

enum class AppTab {
    HOME,
    SCAN,
    CHAT, // PrakritiMitra
    GROWTH,
    COMMUNITY,
    PROFILE
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val sender: Sender,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class Sender { USER, SYSTEM, BOT }
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AppViewModel"
    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.dao())

    // UI Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Database state flows
    val plants: StateFlow<List<TrackedPlant>> = repository.allPlants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scans: StateFlow<List<ScanHistory>> = repository.allScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<CommunityPost>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // PrakritiMitra Chat States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Smart plant recommendation states
    private val _recommendationResult = MutableStateFlow<String?>(null)
    val recommendationResult: StateFlow<String?> = _recommendationResult.asStateFlow()

    private val _isRecommending = MutableStateFlow(false)
    val isRecommending: StateFlow<Boolean> = _isRecommending.asStateFlow()

    // Climate Suitability Checker States
    private val _suitabilityResult = MutableStateFlow<String?>(null)
    val suitabilityResult: StateFlow<String?> = _suitabilityResult.asStateFlow()

    private val _isCheckingSuitability = MutableStateFlow(false)
    val isCheckingSuitability: StateFlow<Boolean> = _isCheckingSuitability.asStateFlow()

    // Soil Analysis States
    private val _soilAnalysisResult = MutableStateFlow<String?>(null)
    val soilAnalysisResult: StateFlow<String?> = _soilAnalysisResult.asStateFlow()

    private val _isAnalyzingSoil = MutableStateFlow(false)
    val isAnalyzingSoil: StateFlow<Boolean> = _isAnalyzingSoil.asStateFlow()

    // Leaf Disease Code States
    private val _isScanningLeaf = MutableStateFlow(false)
    val isScanningLeaf: StateFlow<Boolean> = _isScanningLeaf.asStateFlow()

    private val _leafScanResult = MutableStateFlow<ScanHistory?>(null)
    val leafScanResult: StateFlow<ScanHistory?> = _leafScanResult.asStateFlow()

    // Unified voice speaker (Android Native TTS)
    private var tts: TextToSpeech? = null
    private val _isTtsActive = MutableStateFlow(false)
    val isTtsActive: StateFlow<Boolean> = _isTtsActive.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultStatsExist()
        }

        // Add welcoming bot msg
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Namaste! I am PrakritiMitra, your nature and everyday guidance companion. 🌿 Speak or type any questions regarding plant health, gardening advice, or general topics. How can I assist you today?",
                sender = ChatMessage.Sender.BOT
            )
        )

        // Init TTS
        try {
            tts = TextToSpeech(application) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.getDefault()
                } else {
                    Log.e(TAG, "TTS Initialization failed!")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "TTS error", e)
        }
    }

    // Speak text using TTS
    fun speak(text: String) {
        tts?.let { speech ->
            _isTtsActive.value = true
            speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PrakritiMitraTTS")
            // simple delay flag simulation
            viewModelScope.launch {
                val delayMs = (text.length * 70L).coerceIn(2000L, 10000L)
                kotlinx.coroutines.delay(delayMs)
                _isTtsActive.value = false
            }
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        _isTtsActive.value = false
    }

    // --- Core Action Methods ---

    /**
     * Smart recommendation based on inputs
     */
    fun performRecommendation(
        location: String,
        climate: String,
        soil: String,
        space: String,
        purpose: String
    ) {
        _isRecommending.value = true
        _recommendationResult.value = null

        viewModelScope.launch {
            val prompt = """
                Recommend the top 3 best plants to grow for:
                - Location: $location
                - Climate/Season: $climate
                - Soil Type: $soil
                - Available Space: $space
                - Purpose: $purpose

                Format the output beautifully for a gardener. Start with a lovely structured checklist. 
                Include a mini Calendar suggestion for planting, organic fertilizer suggestions, and the expected growth time.
            """.trimIndent()

            val response = GeminiService.generateContent(
                prompt = prompt,
                systemInstruction = "You are a smart horticulturalist providing optimal planting recommendations based on specific local factors. Use Material Icons references like 🌱, 🪵, ☀️, 💧, 🪴 for decoration."
            )
            _recommendationResult.value = response
            _isRecommending.value = false

            // Earn Green Points!
            repository.addGreenPoints(15)
        }
    }

    /**
     * Climate Suitability score checking
     */
    fun checkClimateSuitability(plant: String, location: String, temperature: String, rainfall: String) {
        _isCheckingSuitability.value = true
        _suitabilityResult.value = null

        viewModelScope.launch {
            val prompt = """
                Conduct a climate suitability audit for cultivating '$plant' in '$location' with average temperature '$temperature' and rainfall '$rainfall'.
                Rate the compatibility score out of 100 first, and then list:
                - Temperature compatibility
                - Moisture risks
                - Best season for planting
                Provide direct, expert, professional gardening recommendations.
            """.trimIndent()

            val response = GeminiService.generateContent(
                prompt = prompt,
                systemInstruction = "You are an expert agro-meteorologist. Start the output with a bold line: 'SUITABILITY SCORE: [score]/100' so the user can easily see it."
            )
            _suitabilityResult.value = response
            _isCheckingSuitability.value = false

            repository.addGreenPoints(10)
        }
    }

    /**
     * Soil analysis from text or image
     */
    fun analyzeSoil(manualInput: String, soilImage: Bitmap?) {
        _isAnalyzingSoil.value = true
        _soilAnalysisResult.value = null

        viewModelScope.launch {
            val prompt = if (soilImage != null) {
                """
                    Analyse the soil from the uploaded image. The user also noted: "$manualInput".
                    Identify:
                    1. Soil Type identification (Clayey, Sandy, Loamy, Silt, etc.)
                    2. Approximate pH range and Nutrient balance
                    3. Organic fertilizer recommendations (compost, rock phosphate, green manure, etc.)
                    4. Soil structure improvement tips
                    Structure your reply clearly under those 4 headers.
                """.trimIndent()
            } else {
                """
                    Analyse the soil based on these user notes: "$manualInput".
                    Identify:
                    1. Soil Type and potential properties
                    2. Essential nutrient enhancements required
                    3. Organic fertilizers suggestions
                    4. Sustainable cultivation advice 
                    Structure your reply clearly under those 4 headers.
                """.trimIndent()
            }

            val response = GeminiService.generateContent(
                prompt = prompt,
                imageBitmap = soilImage,
                systemInstruction = "You are a soil scientist helping organic gardeners enhance their land using local and chemical-free techniques."
            )
            _soilAnalysisResult.value = response
            _isAnalyzingSoil.value = false

            repository.addGreenPoints(20)
        }
    }

    /**
     * Leaf Scanning Disease Detection
     */
    fun scanLeafDisease(image: Bitmap) {
        _isScanningLeaf.value = true
        _leafScanResult.value = null

        viewModelScope.launch {
            val prompt = """
                Carefully diagnose this plant plant leaf leaf-disease image.
                Perform:
                1. Disease identification (or say 'Healthy' if no pests/diseases are visible)
                2. Sickness severity estimation (None, Low, Moderate, High)
                3. Detailed organic treatment suggestions
                4. Pest detection notes

                You MUST return the diagnosis strictly formatted as a JSON object with these exact keys:
                {
                   "disease_name": "the suspected disease name",
                   "status_severity": "Low" or "Moderate" or "High" or "None",
                   "description": "short analysis description text",
                   "remedies": "bullet list of chemical-free organic treatments"
                }
            """.trimIndent()

            val responseString = GeminiService.generateContent(
                prompt = prompt,
                imageBitmap = image,
                systemInstruction = "You are a plant pathologist diagnosing agricultural damage from pictures. Only return a valid JSON block."
            )

            // Parse response
            try {
                val cleaned = GeminiService.cleanJsonMarkdown(responseString)
                val json = JSONObject(cleaned)
                val disease = json.optString("disease_name", "Unknown Sickness")
                val severity = json.optString("status_severity", "None")
                val desc = json.optString("description", "A leaf scan was analyzed.")
                val remedies = json.optString("remedies", "Prune yellow leaves and water moderately.")

                val historyItem = ScanHistory(
                    type = "LEAF",
                    plantName = "Leaf Search",
                    timestamp = System.currentTimeMillis(),
                    title = disease,
                    severity = severity,
                    info = desc,
                    remedies = remedies
                )
                
                // Save history item to DB
                val itemId = repository.insertScan(historyItem)
                _leafScanResult.value = historyItem.copy(id = itemId.toInt())
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing leaf json", e)
                // Fallback structured scan history on error
                val fallbackItem = ScanHistory(
                    type = "LEAF",
                    plantName = "Spotted Leaf Scan",
                    timestamp = System.currentTimeMillis(),
                    title = "Suspicious Leaf Spots",
                    severity = "Moderate",
                    info = responseString.take(400) + "...",
                    remedies = "Apply organic neem oil spray, isolate the plant, and improve light ventilation."
                )
                val itemId = repository.insertScan(fallbackItem)
                _leafScanResult.value = fallbackItem.copy(id = itemId.toInt())
            }

            _isScanningLeaf.value = false
            repository.addGreenPoints(30)
        }
    }

    /**
     * AI Chat Support (PrakritiMitra)
     */
    fun sendChatMessage(text: String, isAudioOutputWanted: Boolean = false) {
        if (text.isBlank()) return

        // Append user chat message
        val userItem = ChatMessage(text = text, sender = ChatMessage.Sender.USER)
        _chatMessages.value = _chatMessages.value + userItem

        _isChatLoading.value = true

        viewModelScope.launch {
            // Context synthesis: send last 8 messages to save tokens
            val contextMsgs = _chatMessages.value.takeLast(9).dropLast(1)
            val instructions = """
                You are PrakritiMitra, an intelligent companion and environmental expert for the EcoFriend Android app.
                You are a highly versatile AI companion: you can act as a fully competent general AI assistant resolving science concepts, educational topics, everyday household logic, study tasks, and productivity prompts, while maintaining deep, world-class specialized expertise in agriculture, plant care, organic farming, garden irrigation, and biodiversity conservation.
                
                Keep your response helpful, friendly, and structured. Use organic emojis 🌸🌱🌾 appropriate to nature.
                You MUST respond in the language used by the user. If they talk in Hindi, Telugu, Tamil, or some other tongue, reply naturally in that selfsame script.
            """.trimIndent()

            val builder = StringBuilder()
            contextMsgs.forEach { m ->
                val author = if (m.sender == ChatMessage.Sender.USER) "User" else "PrakritiMitra"
                builder.append("$author: ${m.text}\n")
            }
            builder.append("User: $text\n")
            builder.append("PrakritiMitra:")

            val inputPrompt = builder.toString()
            val aiResponse = GeminiService.generateContent(
                prompt = inputPrompt,
                systemInstruction = instructions
            )

            // Add response to conversation
            val botItem = ChatMessage(text = aiResponse, sender = ChatMessage.Sender.BOT)
            _chatMessages.value = _chatMessages.value + botItem
            _isChatLoading.value = false

            // Voice synthesis if wanted
            if (isAudioOutputWanted) {
                // strip emojis or excessive markdown for clean voice quality
                val talkPlain = aiResponse.replace(Regex("[*#`_~🌿🌱✨🪵💧☀️🪴🌸🌾🌸🍎🍇🍋🍂🍁🐝🐜🐞🦟🕸🦎🌾🎋🎍🍀☘️🍃🎋🌱🌳🌲🌴]"), "")
                speak(talkPlain.take(300)) // limit read to prevent excessive duration
            }

            // Users get points for engaging with nature learning!
            repository.addGreenPoints(2)
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Namaste! I am PrakritiMitra. Let's restart our green chat! 🌿 Ask me anything about gardens, farming, or daily life. I'll guide you.",
                sender = ChatMessage.Sender.BOT
            )
        )
        stopSpeaking()
    }

    // --- Plant Storage Tasks ---
    fun addTrackedPlant(
        name: String,
        species: String,
        category: String,
        description: String,
        wateringFrequencyDays: Int
    ) {
        viewModelScope.launch {
            val plant = TrackedPlant(
                name = name,
                species = species,
                category = category,
                description = description,
                datePlanted = System.currentTimeMillis(),
                lastWatered = System.currentTimeMillis(),
                waterFrequencyDays = wateringFrequencyDays,
                heightProgressHistory = "2.0",
                heightDatesHistory = System.currentTimeMillis().toString(),
                expectedHeightMax = 40.0f,
                yieldPrediction = "Harvest simulation calculated: 45 days",
                healthScore = 100
            )
            repository.insertPlant(plant)
            repository.addGreenPoints(25)
        }
    }

    fun updatePlantWatering(plant: TrackedPlant) {
        viewModelScope.launch {
            val updated = plant.copy(lastWatered = System.currentTimeMillis())
            repository.updatePlant(plant = updated)
            repository.addGreenPoints(10) // watering rewards!
        }
    }

    fun addPlantHeightLog(plant: TrackedPlant, newHeightCm: Float) {
        viewModelScope.launch {
            val heights = plant.heightProgressHistory + ",$newHeightCm"
            val dates = plant.heightDatesHistory + ",${System.currentTimeMillis()}"
            val roundedScore = (plant.healthScore + 2).coerceAtMost(100)
            val updated = plant.copy(
                heightProgressHistory = heights,
                heightDatesHistory = dates,
                healthScore = roundedScore
            )
            repository.updatePlant(updated)
            repository.addGreenPoints(15) // logs rewards!
        }
    }

    fun deleteTrackedPlant(plant: TrackedPlant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    // --- Discussion Posts Actions ---
    fun submitPost(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            val currentStats = repository.getUserStats()
            val userLvl = currentStats?.level ?: 1
            val badge = when {
                userLvl >= 4 -> "Eco Mastermind"
                userLvl >= 3 -> "Tree Guardian"
                userLvl >= 2 -> "Sapling Protector"
                else -> "Seed Sower"
            }
            val post = CommunityPost(
                username = "GreenEcoWarrior",
                role = badge,
                message = message,
                timestamp = System.currentTimeMillis(),
                likes = 0,
                userLiked = false
            )
            repository.insertPost(post)
            repository.addGreenPoints(15)
        }
    }

    fun toggleLikePost(post: CommunityPost) {
        viewModelScope.launch {
            val isLiked = !post.userLiked
            val delta = if (isLiked) 1 else -1
            val updated = post.copy(
                userLiked = isLiked,
                likes = (post.likes + delta).coerceAtLeast(0)
            )
            repository.updatePost(updated)
        }
    }

    // --- Challenges completion triggers ---
    fun toggleChallenge(index: Int, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateChallengeProgress(index, isChecked)
            if (isChecked) {
                repository.addGreenPoints(30) // points for fully completing daily challenges
            } else {
                repository.addGreenPoints(-30)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}
