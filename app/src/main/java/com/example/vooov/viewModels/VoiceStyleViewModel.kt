package com.example.vooov.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.vooov.data.model.VoiceStyleModel
import com.example.vooov.repositories.VoiceStyleRepository
import java.io.IOException

class VoiceStyleViewModel {
    private val repository = VoiceStyleRepository()

    var voiceStylelist = MutableLiveData<MutableList<VoiceStyleModel>>()

    suspend fun fetchVoiceStyles(){
        try{
            val response = repository.readVoiceStylesData()
            if (response.isSuccessful){
                voiceStylelist.value = response.body()// value need a MutableLiveData instead of a simple mutableList
            } else {
                when (response.code()) {
                    in 400..499 -> {
                        Log.i(ContentValues.TAG, "${response.code()} Message: ${response.errorBody()?.string()}")
                    }
                    in 500..599 -> {
                        Log.i(ContentValues.TAG, "${response.code()} Message: ${response.errorBody()?.string()}")
                    }
                    else -> {
                        Log.i(ContentValues.TAG, "Une autre erreur")
                    }
                }
                throw IOException("Error fetching voiceStyleId")
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error fetching voiceStyle", e)
        }
    }


    val voiceStyle = MutableLiveData<VoiceStyleModel>()

    suspend fun fetchOneVoiceStyle(voiceStyleId: Int) {
        try {
            val response = repository.readOneVoiceStyleData(voiceStyleId)
            if (response.isSuccessful) {
                val responseData = response.body()
                voiceStyle.value = responseData ?: VoiceStyleModel()
            } else {
                when (response.code()) {
                    in 400..499 -> {
                        Log.i(ContentValues.TAG, "${response.code()} Message: ${response.errorBody()?.string()}")
                    }
                    in 500..599 -> {
                        Log.i(ContentValues.TAG, "${response.code()} Message: ${response.errorBody()?.string()}")
                    }
                    else -> {
                        Log.i(ContentValues.TAG, "Une autre erreur")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error fetching voiceStyle", e)
        }
    }
}