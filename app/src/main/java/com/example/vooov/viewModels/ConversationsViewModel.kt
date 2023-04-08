package com.example.vooov.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vooov.data.model.ConversationsModel
import com.example.vooov.data.model.UserModel
import com.example.vooov.repositories.ConversationsRepository
import com.example.vooov.repositories.UserRepository
import retrofit2.Response
import java.util.*

class ConversationsViewModel : ViewModel() {
    private val repository = ConversationsRepository()

    suspend fun createConversation(contactUuid: String, selfUuid: String) {
        val randomUuid = UUID.randomUUID().toString()
        val user1: Response<UserModel> = UserRepository().readOneUserData(selfUuid)
        val user2: Response<UserModel> = UserRepository().readOneUserData(contactUuid)

        val conversation = ConversationsModel(
            null,
            user1.body()?.id,
            user2.body()?.id,
            randomUuid,
            "${user1.body()?.pseudo} ${user2.body()?.pseudo}",
            Date().toString(),
            Date().toString()
        )
        val response = repository.createConversationData(conversation)
        if (response.isSuccessful) {
            Log.i(ContentValues.TAG, "Conversation créée avec succès")
        } else {
            Log.e(ContentValues.TAG, "Erreur lors de la création de la conversation")
            Log.i(ContentValues.TAG, "$conversation")
            Log.i(ContentValues.TAG, "${response.code()} Message: ${response.errorBody()?.string()}")

        }
    }

    var conversationList = MutableLiveData<MutableList<ConversationsModel>>()

    suspend fun fetchConversations(selfUuid: String) {
        val response = repository.readConversationData(selfUuid)
        if (response.isSuccessful) {
            conversationList.value = response.body()
        }
    }

    val conversation = MutableLiveData<ConversationsModel>()

    suspend fun fetchOneConversation(contactUuid: String, selfUuid: String) {
        // Récupérer la liste de conversations
        fetchConversations(selfUuid)
        var contact: Response<UserModel> = UserRepository().readOneUserData(contactUuid)
        // Trouver la conversation qui correspond à contactUuid
        conversationList.value?.let { list ->
            val filteredList = list.filter { conversation ->
                (conversation.sender_id == contact.body()?.id || conversation.receiver_id == contact.body()?.id)
            }

            if (filteredList.isNotEmpty()) {
                conversation.value = filteredList[0] // Récupérer la première conversation correspondante
            } else {
                createConversation(contactUuid, selfUuid)
            }
        }
    }

    val conversationSelected = MutableLiveData<ConversationsModel>()

    suspend fun fetchConversationByUuid(conversationUuid: String){
        val response = repository.readOneConversationData(conversationUuid)
        if(response.isSuccessful){
            conversationSelected.value = response.body()
        }
    }

    val conversationsUserList = MutableLiveData<MutableList<ConversationsModel>>()

    suspend fun showAllUserConversations(selfUuid: String, selfUuidSame: String){
        val response = repository.readUserConversationsData(selfUuid, selfUuidSame)
        if(response.isSuccessful){
            conversationsUserList.value = response.body()
            Log.i(ContentValues.TAG, "conversations récupérées")
        } else {
            Log.e(ContentValues.TAG, "Erreur lors de la récupération des conversations")
            Log.i(ContentValues.TAG, "$conversationList")
            Log.i(ContentValues.TAG, "${response.code()} Message: ${response.errorBody()?.string()}")
        }

    }
}

