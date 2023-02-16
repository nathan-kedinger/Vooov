package com.example.vooov.data.model

data class RecordModel(
    val uuid: String = "null",
    val id:  Int? = null,
    val artist_uuid: String? = "Doe",
    val title: String = "Jon",
    val length: Int = 0,
    val number_of_plays: Int = 0,
    val number_of_moons: Int = 0,
    val voice_style: String = "",
    var kind: String = "",
    val description: String = "",
    var created_at: String = "",
    val updated_at: String = "",
)
