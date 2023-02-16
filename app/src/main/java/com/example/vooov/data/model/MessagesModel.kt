package com.example.vooov.data.model

data class MessagesModel(
    val uuid: String,
    val conversation_uuid: String,
    val sender: String,
    val receiver: String,
    val body: String,
    val seen: Int,
    val send_at: String
)
