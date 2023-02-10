package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.databinding.FragmentRecordBinding
import com.example.vooov.databinding.FragmentWalletBinding

class RecordFragment (
): Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // To other fragments

        binding.recordPageGoToArtist.setOnClickListener {
            // Adding identifications arguments here

            findNavController().navigate(R.id.action_recordPageFragment_to_artistProfilFragment)

        }

        return view

    }
}