package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.adapters.ConversationsAdapter
import com.example.vooov.adapters.MessageAdapter
import com.example.vooov.databinding.FragmentConversationsBinding
import com.example.vooov.databinding.FragmentWalletBinding
import com.example.vooov.viewModels.ConversationsViewModel
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.MessageViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConversationsFragment (
): Fragment() {
    private var _binding: FragmentConversationsBinding? = null
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
        _binding = FragmentConversationsBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        val recycler = binding.conversationsRecycler

        val userUuid = CurrentUser(requireContext()).readString("uuid")
        conversationViewModel = ViewModelProvider(this).get(ConversationsViewModel::class.java)
        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        // To other fragments

        // Adding recycler here. Going from items to the selected conversation
        CoroutineScope(Dispatchers.Main).launch {
            if (userUuid != null){
                conversationViewModel.showAllUserConversations(userUuid, userUuid)
            }
        }

        conversationViewModel.conversationsUserList.observe(viewLifecycleOwner, Observer { conversationsUserList ->
            if(conversationsUserList != null){
                if(userUuid != null){
                    val sortedConversations = conversationsUserList.sortedBy { it.updated_at }
                    recycler.adapter = ConversationsAdapter(
                        this@ConversationsFragment,
                        sortedConversations,
                        R.layout.item_conversation,
                        findNavController(),
                        viewLifecycleOwner,
                        conversationViewModel,
                        userUuid
                    )
                }

            }
        })

        return view

    }
}