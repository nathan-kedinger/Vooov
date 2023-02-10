package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.databinding.FragmentArtistProfileBinding
import com.example.vooov.databinding.FragmentWalletBinding

class ArtistProfileFragment (
): Fragment() {
    private var _binding: FragmentArtistProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArtistProfileBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // To other fragments
        binding.artistProfilAskForVooov.setOnClickListener{
            //adding the arguments to match the conversation here
            findNavController().navigate(R.id.action_artistProfileFragment_to_messageFragment)
        }

        binding.artistProfilRecordList.setOnClickListener{
            findNavController().navigate(R.id.action_artistProfileFragment_to_recyclerRecordFragment)
        }

        return view

    }
}