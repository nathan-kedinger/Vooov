package com.example.vooov.data.model

data class VoiceStyleModel(
    val id: Int? = null,
    val voice_style: String = ""
) {
    override fun toString(): String {
        return voice_style
    }
}
