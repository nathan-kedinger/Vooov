package com.example.vooov.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.vooov.R
import com.example.vooov.data.model.ConversationsModel
import com.example.vooov.fragments.ConversationsFragment
import com.example.vooov.viewModels.ConversationsViewModel
import com.example.vooov.viewModels.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConversationsAdapter (
    val context: ConversationsFragment,
    val conversationList: List<ConversationsModel>,
    val layoutId: Int,
    val navController: NavController,
    val lifeCycleOwner: LifecycleOwner,
    val conversationsViewModel: ConversationsViewModel,
    val currentUserUuid: String

) : RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePicture = view.findViewById<ImageView  >(R.id.conversations_item_profile_picture)
        val conversationName = view.findViewById<TextView>(R.id.conversations_item_name)
        val lastMessage = view.findViewById<TextView>(R.id.conversations_item_last_update)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentConversation: ConversationsModel = conversationList[position]

        holder.conversationName.text = currentConversation.title
        holder.lastMessage.text = currentConversation.updated_at

        val sender = currentConversation.sender
        val receiver = currentConversation.receiver
        lateinit var  contactUuid: String
        if (sender == currentUserUuid){
            contactUuid = receiver
        } else {
            contactUuid = sender
        }

        holder.itemView.setOnClickListener {
            val selectedConversationToMessage = Bundle()
            if (currentConversation.uuid != null){
                selectedConversationToMessage.putString("toSendMessageFragment", currentConversation.uuid)
                selectedConversationToMessage.putString("toSendMessageContactUuid", contactUuid)
                navController.navigate(R.id.messageFragment, selectedConversationToMessage)
            }
        }

    }

    override fun getItemCount(): Int {
        return conversationList.size
    }
}