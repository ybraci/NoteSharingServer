package com.example.data
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val message: String
)
