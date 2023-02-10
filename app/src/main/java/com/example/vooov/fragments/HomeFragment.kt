package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.databinding.FragmentHomeBinding
import com.example.vooov.databinding.FragmentWalletBinding

class HomeFragment (
): Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // To other fragments

        binding.homeRecordSendMessage.setOnClickListener{
            // Adding identifications and conversations crations code here
            findNavController().navigate(R.id.action_homeFragment_to_messageFragment)
        }

        binding.homeRecordPlus.setOnClickListener {
            // Adding argument to get to the good record here
            findNavController().navigate(R.id.action_homeFragment_to_recordPageFragment)
        }


        return view

    }
}