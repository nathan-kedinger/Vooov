package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.databinding.FragmentPersonalProfileBinding
import com.example.vooov.databinding.FragmentWalletBinding

class PersonalProfileFragment (
): Fragment() {
    private var _binding: FragmentPersonalProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalProfileBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // To other fragments

        binding.personalProfilCommunity.setOnClickListener {
            //  Adding arguments here to get the good conversation list
            findNavController().navigate(R.id.action_personalProfileFragment_to_communityFragment)
        }

        binding.personalProfilMyRecords.setOnClickListener {
            // Adding argumetns here to get the personal record list
            findNavController().navigate(R.id.action_personalProfileFragment_to_recyclerRecordFragment)
        }

        binding.personalProfilWallet.setOnClickListener {
            // Adding arguments here
            findNavController().navigate(R.id.action_personalProfileFragment_to_walletFragment)

        }

        return view

    }
}