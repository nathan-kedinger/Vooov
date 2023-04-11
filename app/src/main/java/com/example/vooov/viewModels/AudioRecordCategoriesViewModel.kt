package com.example.vooov.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.vooov.data.model.AudioRecordCategoriesModel
 import com.example.vooov.repositories.AudioRecordCategoriesRepository
import java.io.IOException

class AudioRecordCategoriesViewModel {
    private val repository = AudioRecordCategoriesRepository()

    var audioRecordCategorieslist = MutableLiveData<MutableList<AudioRecordCategoriesModel>>()

    suspend fun fetchAudioRecordCategories(){
        try{
            val response = repository.readAudioRecordCategoriesData()
            if (response.isSuccessful){
                audioRecordCategorieslist.value = response.body()// value need a MutableLiveData instead of a simple mutableList
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
                throw IOException("Error fetching audioRecordCategories")
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error fetching audioRecordCategories", e)
        }
    }


    val audioRecordCategory = MutableLiveData<AudioRecordCategoriesModel>()

    suspend fun fetchOneAudioRecordCategory(audioRecordCategoryId: Int) {
        try {
            val response = repository.readOneAudioRecordCategoryData(audioRecordCategoryId)
            if (response.isSuccessful) {
                val responseData = response.body()
                audioRecordCategory.value = responseData ?: AudioRecordCategoriesModel()
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
            Log.e(ContentValues.TAG, "Error fetching audioRecordCategory", e)
        }
    }
}