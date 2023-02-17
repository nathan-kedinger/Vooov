package com.example.vooov.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vooov.data.model.RecordModel
import com.example.vooov.data.model.UserModel
import com.example.vooov.repositories.RecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class RecordsViewModel: ViewModel() {
    private val repository = RecordRepository()

    fun createRecord(record: RecordModel) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = repository.createRecordData(record)
                if (response.isSuccessful) {
                    // Handle response
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
                Log.e(ContentValues.TAG, "Error creating record", e)
            }
        }
    }


    var recordList = MutableLiveData<MutableList<RecordModel>>()

    suspend fun fetchRecord(){
        try{
            val response = repository.readRecordData()
            if (response.isSuccessful){
                recordList.value = response.body()// value need a MutableLiveData instead of a simple mutableList
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
                throw IOException("Error fetching record")
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error fetching record", e)
        }
    }


    val record = MutableLiveData<RecordModel>()

    suspend fun fetchOneRecord(recordId: Int) {
        try {
            val response = repository.readOneRecordData(recordId)
            if (response.isSuccessful) {
                val responseData = response.body()
                record.value = responseData ?: RecordModel()
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
            Log.e(ContentValues.TAG, "Error fetching record", e)
        }
    }

    fun updateRecord(recordUuid: String?, recordModel: RecordModel) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = repository.updateRecordData(recordUuid, recordModel)
                if (response.isSuccessful) {
                    // handle successful response
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
                Log.e(ContentValues.TAG, "Error creating record", e)
            }
        }
    }

    fun deleteRecord(recordUuid: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = repository.deleteRecordData(recordUuid)
                if (response.isSuccessful) {
                    // handle successful response
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
                Log.e(ContentValues.TAG, "Error creating record", e)
            }
        }
    }
}