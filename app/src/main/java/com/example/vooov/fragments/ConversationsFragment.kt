package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vooov.databinding.FragmentConversationsBinding
import com.example.vooov.databinding.FragmentWalletBinding

class ConversationsFragment (
): Fragment() {
    private var _binding: FragmentConversationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConversationsBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // To other fragments

        // Adding recycler here. Going from items to the selected conversation

        return view

    }
}