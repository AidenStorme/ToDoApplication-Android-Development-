package be.aidenstorme.todoapplication.models

import kotlinx.serialization.Serializable

@Serializable
data class BoredActivityResponse(
    val activity: String = "",
    val accessibility: String = "",
    val type: String = "",
    val participants: Int = 1,
    val price: Double = 0.0,
    val link: String = "",
    val key: String = ""
)
