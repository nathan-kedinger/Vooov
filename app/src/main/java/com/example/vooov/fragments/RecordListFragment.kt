package com.example.vooov.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vooov.databinding.FragmentRecordListBinding
import com.example.vooov.databinding.FragmentWalletBinding

class RecordListFragment (
): Fragment() {
    private var _binding: FragmentRecordListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordListBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // To other fragments

        // Adding a link to record page from the recyclerview here

        return view

    }
}