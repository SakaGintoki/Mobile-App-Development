package com.filkom.designimplementation.model.data.sitter

data class Sitter(
    val id: String = "",
    val name: String = "",
    val experience: String = "",
    val location: String = "",
    val completedJobs: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val price: Double = 0.0,
    val imageUrl: String = "",
    val specialty: String = "Pengasuh Anak",
    val availableDays: List<String> = emptyList(),
    val packages: List<String> = listOf("1 Hari", "3 Hari", "5 Hari")
)