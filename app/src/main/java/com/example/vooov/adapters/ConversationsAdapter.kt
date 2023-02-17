package com.example.vooov.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
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
    val lifeCycleOwner: LifecycleOwner,
    val conversationViewModel: ConversationsViewModel,
    val currentUser: String

) : RecyclerView.Adapter<ConversationsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderName = view.findViewById<TextView>(R.id.message_item_sender)
        val bodyMessage = view.findViewById<TextView>(R.id.message_item_body)
        val sended_at = view.findViewById<TextView>(R.id.message_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentConversation: ConversationsModel = conversationList[position]


        CoroutineScope(Dispatchers.Main).launch {
                conversationViewModel.fetchConversations(currentUser)
        }
        conversationViewModel.conversationList.observe(lifeCycleOwner, Observer { conversationList ->
            if (user != null) {
                holder.senderName.text = user.pseudo
            }
        })


        holder.bodyMessage.text = currentMessage.body
        holder.sended_at.text = currentMessage.send_at
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}