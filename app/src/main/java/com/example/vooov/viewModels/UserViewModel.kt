package com.example.vooov.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vooov.data.model.UserModel
import com.example.vooov.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel: ViewModel() {
    private val repository = UserRepository()

    fun createUser(user: UserModel) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = repository.createUserData(user)
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


    var userList = MutableLiveData<MutableList<UserModel>>()

    suspend fun fetchUser(){
        try{
            val response = repository.readUserData()
            if (response.isSuccessful){
                userList.value = response.body()// value need a MutableLiveData instead of a simple mutableList
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


    val user = MutableLiveData<UserModel>()

    suspend fun fetchOneUser(userUuid: String) {
        //try {
            val response = repository.readOneUserData(userUuid)
            if (response.isSuccessful) {
                val responseData = response.body()
                user.value = responseData ?: UserModel()
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
                }}
                /*throw IOException("Error fetching record")
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error fetching record", e)
        }*/
    }

    fun updateUser(user: UserModel) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = repository.updateUserData(user)
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

    fun deleteUser(userUuid: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = repository.deleteUserData(userUuid)
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