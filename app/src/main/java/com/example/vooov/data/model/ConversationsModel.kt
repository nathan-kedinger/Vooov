package com.example.vooov.data.model

data class ConversationsModel(
    val id: Int?,
    val sender_id: Int?,
    val receiver_id: Int?,
    val uuid: String,
    val title: String,
    val created_at: String,
    val updated_at: String,
)
