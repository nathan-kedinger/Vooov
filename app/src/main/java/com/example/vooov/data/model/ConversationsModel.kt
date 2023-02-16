package com.example.vooov.data.model

data class ConversationsModel(
    val uuid: String,
    val sender: String,
    val receiver: String,
    val title: String,
    val created_at: String,
    val updated_at: String,
)
