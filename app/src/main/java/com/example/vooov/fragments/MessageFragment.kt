package com.example.vooov.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.adapters.MessageAdapter
import com.example.vooov.databinding.FragmentMessageBinding
import com.example.vooov.viewModels.ConversationsViewModel
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.MessageViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageFragment (
): Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private lateinit var conversationViewModel: ConversationsViewModel
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(layoutInflater, container, false)

        val view = binding.root
        val recycler = binding.messageFragmentRecycler
        conversationViewModel = ViewModelProvider(this).get(ConversationsViewModel::class.java)
        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val conversationUuid: String = arguments?.getString("toSendMessageFragment")!!
        val contactUuid: String = arguments?.getString("toSendMessageContactUuid")!!
        val selfUuid = CurrentUser(requireContext()).readString("uuid")


        CoroutineScope(Dispatchers.Main).launch {
            messageViewModel.showAllConversationMessages(conversationUuid)
        }

        messageViewModel.messageList.observe(viewLifecycleOwner, Observer { messageList ->
            if(messageList != null){
                val sortedMessages = messageList.sortedBy { it.send_at }
                recycler.adapter = MessageAdapter(
                    this@MessageFragment,
                    sortedMessages,
                    R.layout.item_message,
                    viewLifecycleOwner,
                    userViewModel,
                )
                recycler.scrollToPosition(messageList.size-1)
            }
        })

        binding.messageFragmentSendButton.setOnClickListener {
            val bodyMessage = binding.messageFragmentMessageBody.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                if (selfUuid != null) {
                    Log.i(ContentValues.TAG, "Creating message with body: $bodyMessage")
                    messageViewModel.createMessage(
                        contactUuid,
                        selfUuid,
                        bodyMessage,
                        conversationUuid
                    )
                    val toSendMessageArgs = Bundle()
                    toSendMessageArgs.putString(
                        "toSendMessageFragment",
                        conversationUuid
                    )
                    toSendMessageArgs.putString(
                        "toSendMessageContactUuid",
                        contactUuid
                    )
                    findNavController().navigate(
                        R.id.action_messageFragment_self,
                        toSendMessageArgs
                    )
                } else {
                    Log.i(ContentValues.TAG, "Self UUID is null")
                }
            }

        }
        // To other fragments
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}