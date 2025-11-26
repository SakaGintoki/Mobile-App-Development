package com.filkom.designimplementation.model.data.daycare

data class Daycare(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distanceInKm: Float? = null,
    val price: Double = 0.0,
    val priceUnit: String = "Hari", // "Hari", "Bulan", "Tahun"
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val imageUrl: String = "",
    val facilities: List<String> = emptyList(), // Contoh: ["AC", "Kasur", "Makan", "CCTV"]
    val openingHours: String = "08.00 - 17.00 WIB",
    val ageRange: String = "6 bulan - 5 tahun",
    val bookingCount: Int = 0 // Untuk tracking popularitas
)