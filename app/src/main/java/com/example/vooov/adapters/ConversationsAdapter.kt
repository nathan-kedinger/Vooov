package com.example.vooov.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.vooov.R
import com.example.vooov.data.model.ConversationsModel
import com.example.vooov.fragments.ConversationsFragment
import com.example.vooov.viewModels.ConversationsViewModel

class ConversationsAdapter (
    val context: ConversationsFragment,
    val conversationList: List<ConversationsModel>,
    val layoutId: Int,
    val navController: NavController,
    val lifeCycleOwner: LifecycleOwner,
    val conversationsViewModel: ConversationsViewModel,
    val currentUserId: Int

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

        val sender = currentConversation.sender_id
        val receiver = currentConversation.receiver_id
        var contactId: Int? = 0
        if (sender == currentUserId){
            contactId = receiver
        } else {
            contactId = sender
        }

        holder.itemView.setOnClickListener {
            val selectedConversationToMessage = Bundle()
            if (currentConversation.uuid != null){
                selectedConversationToMessage.putString("toSendMessageFragment", currentConversation.uuid)
                selectedConversationToMessage.putInt("toSendMessageContactUuid", contactId!!)
                navController.navigate(R.id.messageFragment, selectedConversationToMessage)
            }
        }

    }

    override fun getItemCount(): Int {
        return conversationList.size
    }
}