package com.jalsanchay.utils

/**
 * Manages water conservation and rainwater harvesting tips
 */
data class WaterTip(
    val id: Int,
    val title: String,
    val description: String,
    val category: String, // "harvesting", "conservation", "maintenance", "best_practice"
    val difficulty: String, // "easy", "medium", "hard"
    val emoji: String
)

object WaterTipsManager {
    
    val TIPS = listOf(
        WaterTip(
            1,
            "Clean Your Roof Gutters",
            "Keep roof gutters and downpipes clean to prevent debris from entering your water tank. Clean before rainy season.",
            "maintenance",
            "easy",
            "🧹"
        ),
        WaterTip(
            2,
            "Install First Flush Diverter",
            "A first flush diverter automatically discards the first portion of rainfall to remove dirt and dust from the roof.",
            "harvesting",
            "medium",
            "🔧"
        ),
        WaterTip(
            3,
            "Use Tank Screens",
            "Install mesh screens on tank inlets to filter out leaves and insects from entering your water storage.",
            "maintenance",
            "easy",
            "🪤"
        ),
        WaterTip(
            4,
            "Monitor Tank Water Level",
            "Regularly check your tank water level to know when to harvest more water and plan water usage.",
            "best_practice",
            "easy",
            "📏"
        ),
        WaterTip(
            5,
            "Use Harvested Water Wisely",
            "Prioritize using harvested rainwater for non-potable uses: gardens, cleaning, toilets, and laundry.",
            "conservation",
            "easy",
            "💡"
        ),
        WaterTip(
            6,
            "Add Sediment Filter",
            "Install a sediment filter tank before your main storage to remove particles and improve water quality.",
            "harvesting",
            "medium",
            "⏳"
        ),
        WaterTip(
            7,
            "Mulch Your Garden",
            "Use mulch in gardens to reduce water evaporation and retain soil moisture longer.",
            "conservation",
            "easy",
            "🌾"
        ),
        WaterTip(
            8,
            "Fix Leaking Taps",
            "A dripping tap wastes 3,000 liters per month! Fix leaks immediately to conserve water.",
            "conservation",
            "easy",
            "🚰"
        ),
        WaterTip(
            9,
            "Install Low-Flow Fixtures",
            "Replace shower heads and faucets with low-flow versions to reduce water consumption by 25-60%.",
            "conservation",
            "medium",
            "💧"
        ),
        WaterTip(
            10,
            "Use Rainwater for Toilet",
            "Toilets use 30% of household water. Use harvested rainwater for flushing to save significantly.",
            "best_practice",
            "medium",
            "🚽"
        ),
        WaterTip(
            11,
            "Create Water Harvesting Ponds",
            "Build small ponds to capture and store excess rainwater during monsoon season.",
            "harvesting",
            "hard",
            "🏞️"
        ),
        WaterTip(
            12,
            "Use Drip Irrigation",
            "Drip irrigation delivers water directly to plant roots, reducing waste by up to 50% compared to sprinklers.",
            "conservation",
            "medium",
            "💦"
        ),
        WaterTip(
            13,
            "Check Tank Overflow",
            "Ensure tank overflow is properly managed to channel excess water to recharge groundwater or secondary use.",
            "maintenance",
            "easy",
            "🌊"
        ),
        WaterTip(
            14,
            "Regular Tank Cleaning",
            "Clean your water tank every 2-3 years to remove silt and maintain water quality.",
            "maintenance",
            "medium",
            "🧼"
        ),
        WaterTip(
            15,
            "Harvest From Terraces",
            "Terraced or tiered roofs can significantly increase water collection surface area.",
            "harvesting",
            "hard",
            "🏢"
        )
    )
    
    fun getTipOfTheDay(): WaterTip {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val index = dayOfYear % TIPS.size
        return TIPS[index]
    }
    
    fun getTipsByCategory(category: String): List<WaterTip> {
        return TIPS.filter { it.category == category }
    }
    
    fun getTipsByDifficulty(difficulty: String): List<WaterTip> {
        return TIPS.filter { it.difficulty == difficulty }
    }
    
    fun getRandomTip(): WaterTip {
        return TIPS.random()
    }
    
    fun searchTips(query: String): List<WaterTip> {
        return TIPS.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }
}
