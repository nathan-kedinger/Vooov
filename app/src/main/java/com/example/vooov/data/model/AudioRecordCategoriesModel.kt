package com.example.vooov.data.model

data class AudioRecordCategoriesModel(
    val id: Int? = null,
    val name: String = ""
) {
    override fun toString(): String {
        return name
    }
}
