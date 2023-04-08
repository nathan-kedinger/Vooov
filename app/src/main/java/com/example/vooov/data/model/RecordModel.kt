package com.example.vooov.data.model

data class RecordModel(
    val id: Int? = null,
    val artist_id: Int? = null,
    var categories_id: Int? = null,
    val voice_style_id: Int? = null,
    val uuid: String = "null",
    val title: String = "Jon",
    val length: Int = 0,
    val number_of_plays: Int = 0,
    var number_of_moons: Int = 0,
    val description: String = "",
    var created_at: String = "",
    val updated_at: String = "",
)
