package com.example.vooov.data.model

data class MessagesModel(
    val id: Int?,
    val sender_id: Int?,
    val receiver_id: Int?,
    val uuid: String,
    val conversation_uuid: String,
    val body: String,
    val seen: Int,
    val send_at: String
)
