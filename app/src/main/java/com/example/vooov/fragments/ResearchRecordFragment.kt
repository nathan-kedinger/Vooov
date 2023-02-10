package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vooov.databinding.FragmentResearchRecordBinding
import com.example.vooov.databinding.FragmentWalletBinding

class ResearchRecordFragment (
): Fragment() {
    private var _binding: FragmentResearchRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResearchRecordBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // To other fragments


        // Adding links from recyclerview to record pages

        return view

    }
}