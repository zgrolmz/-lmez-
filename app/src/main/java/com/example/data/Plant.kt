package com.example.data

enum class PlantCategory(val displayName: String) {
    ENDEMIC_PLANT("Endemik Bitki"),
    SPICE("Yerel Baharat")
}

data class Plant(
    val id: String,
    val name: String,
    val scientificName: String,
    val category: PlantCategory,
    val region: String,
    val description: String,
    val benefits: String,
    val soilRequirements: String,
    val wateringInstructions: String,
    val sunlight: String,
    val sowingSeason: String,
    val conservationStatus: String, // VU, EN, CR, LC, NT
    val difficultyLevel: String, // Kolay, Orta, Zor
    val wateringFrequency: Int, // 1 to 3 scale (1: Az, 2: Orta, 3: Sık)
    val lightType: String // "Güneşli", "Yarı Gölge", "Gölge"
) {
    // Helper to get translated standard conservation status
    val statusText: String
        get() = when (conservationStatus) {
            "CR" -> "Kritik Tehlikede (CR)"
            "EN" -> "Tehlikede (EN)"
            "VU" -> "Hassas (VU)"
            "NT" -> "Tehdide Yakın (NT)"
            "LC" -> "Düşük Riskli (LC)"
            else -> "Belirsiz"
        }
}
