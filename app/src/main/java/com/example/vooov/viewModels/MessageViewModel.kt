package com.example.vooov.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vooov.data.model.MessagesModel
import com.example.vooov.data.model.UserModel
import com.example.vooov.repositories.MessagesRepository
import com.example.vooov.repositories.UserRepository
import retrofit2.Response
import java.util.*

class MessageViewModel: ViewModel() {
    private val repository = MessagesRepository()

    suspend fun createMessage(contactUuid: String, selfUuid: String, body: String, conversationUuid: String){
        val randomUuid = UUID.randomUUID().toString()
        val user1: Response<UserModel> = UserRepository().readOneUserData(selfUuid)
        val user2: Response<UserModel> = UserRepository().readOneUserData(contactUuid)
        val message = MessagesModel(
            null,
            user1.body()?.id,
            user2.body()?.id,
            randomUuid,
            conversationUuid,
            body,
            0,
            Date().toString()
        )
        val response = repository.createMessageData(message)
        if (response.isSuccessful){
            Log.i(ContentValues.TAG, "message envoyé avec succès")
        } else {
            Log.e(ContentValues.TAG, "Erreur lors de la création du message")
            Log.i(ContentValues.TAG, "$message")
            Log.i(ContentValues.TAG, "${response.code()} Message: ${response.errorBody()?.string()}")
        }
    }

    val messageList = MutableLiveData<MutableList<MessagesModel>>()

    suspend fun showAllConversationMessages(conversationUuid: String){
        val response = repository.readOneConversationMessagesData(conversationUuid)
        if(response.isSuccessful){
            messageList.value = response.body()
        }

    }

}