package com.filkom.designimplementation.model.data.consultation

import com.google.firebase.firestore.PropertyName

data class Doctor(
    val id: String = "",
    val name: String = "",
    val specialization: String = "Dokter Umum",
    val experience: String = "0 Tahun",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val price: Double = 0.0,
    val imageUrl: String = "",
    val location: String = "",
    val patientCount: Int = 0,

    @get:PropertyName("isProfessional")
    val isProfessional: Boolean = true
)