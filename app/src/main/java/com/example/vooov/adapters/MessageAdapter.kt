package com.example.vooov.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.vooov.R
import com.example.vooov.data.model.MessagesModel
import com.example.vooov.fragments.MessageFragment
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageAdapter(
    val context: MessageFragment,
    val messageList: List<MessagesModel>,
    val layoutId: Int,
    val lifeCycleOwner: LifecycleOwner,
    val userViewModel: UserViewModel

    ) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

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
        val currentMessage: MessagesModel = messageList [position]

        CoroutineScope(Dispatchers.Main).launch {
            userViewModel.fetchOneUserById(currentMessage.sender_id)
        }
        userViewModel.user.observe(lifeCycleOwner, Observer { user ->
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