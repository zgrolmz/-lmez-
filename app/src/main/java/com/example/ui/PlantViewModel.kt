package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface ChatState {
    object Idle : ChatState
    object Loading : ChatState
    data class Error(val message: String) : ChatState
}

class PlantViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val gardenRepository = GardenRepository(db.gardenPlantDao())

    // --- Search & Filter States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<PlantCategory?>(null)
    val selectedCategory: StateFlow<PlantCategory?> = _selectedCategory.asStateFlow()

    // Combined live list of filtered plants from repository
    val filteredPlants: StateFlow<List<Plant>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        var list = PlantRepository.searchPlants(query)
        if (category != null) {
            list = list.filter { it.category == category }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlantRepository.plants
    )

    // --- Detailed View State ---
    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant.asStateFlow()

    // --- Virtual Garden States ---
    val gardenPlants: StateFlow<List<GardenPlant>> = gardenRepository.allGardenPlants
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current plant active in the Add-To-Garden dialog flow
    private val _plantToAddToGarden = MutableStateFlow<Plant?>(null)
    val plantToAddToGarden: StateFlow<Plant?> = _plantToAddToGarden.asStateFlow()

    // --- AI Botanist Chat States ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Merhaba! Ben Yapay Zeka Botanikçiniz. 🌿\n\nTürkiye'nin muhteşem endemik bitkileri, yerel baharatları veya saksınızda/bahçenizde yetiştirmek istediğiniz bitkilerle ilgili her şeyi bana sorabilirsiniz. Nasıl yardımcı olabilirim?",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    // --- Search & Filter Actions ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: PlantCategory?) {
        _selectedCategory.value = category
    }

    // --- Detailed Plant Actions ---
    fun selectPlant(plant: Plant?) {
        _selectedPlant.value = plant
    }

    // --- Virtual Garden Actions ---
    fun setPlantToAddToGarden(plant: Plant?) {
        _plantToAddToGarden.value = plant
    }

    fun addToGarden(plant: Plant, nickname: String, notes: String) {
        viewModelScope.launch {
            val gardenPlant = GardenPlant(
                plantId = plant.id,
                customName = nickname.ifBlank { plant.name },
                notes = notes
            )
            gardenRepository.insert(gardenPlant)
            _plantToAddToGarden.value = null // Close dialog
        }
    }

    fun waterPlant(gardenPlant: GardenPlant) {
        viewModelScope.launch {
            val updated = gardenPlant.copy(lastWatered = System.currentTimeMillis())
            gardenRepository.update(updated)
        }
    }

    fun updateGardenNotes(gardenPlant: GardenPlant, newNotes: String) {
        viewModelScope.launch {
            val updated = gardenPlant.copy(notes = newNotes)
            gardenRepository.update(updated)
        }
    }

    fun removeFromGarden(gardenPlant: GardenPlant) {
        viewModelScope.launch {
            gardenRepository.delete(gardenPlant)
        }
    }

    // --- Bot Chat Actions ---
    fun sendMessageToBot(text: String) {
        if (text.isBlank()) return

        // Add user message locally
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.update { it + userMsg }
        _chatState.value = ChatState.Loading

        viewModelScope.launch {
            // Context history mapped as string/user pairs for API calls
            val history = _chatMessages.value.map { it.text to it.isUser }

            val reply = GeminiService.getBotResponse(history)
            
            _chatMessages.update {
                it + ChatMessage(text = reply, isUser = false)
            }
            _chatState.value = ChatState.Idle
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Merhaba! Ben Yapay Zeka Botanikçiniz. 🌿\n\nTürkiye'nin muhteşem endemik bitkileri, yerel baharatları veya saksınızda/bahçenizde yetiştirmek istediğiniz bitkilerle ilgili her şeyi bana sorabilirsiniz. Nasıl yardımcı olabilirim?",
                isUser = false
            )
        )
        _chatState.value = ChatState.Idle
    }
}
