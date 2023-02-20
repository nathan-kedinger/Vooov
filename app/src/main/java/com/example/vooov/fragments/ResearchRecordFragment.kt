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
import com.example.vooov.adapters.RecordAdapter
import com.example.vooov.adapters.SearchRecordAdapter
import com.example.vooov.databinding.FragmentResearchRecordBinding
import com.example.vooov.databinding.FragmentWalletBinding
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResearchRecordFragment (
): Fragment() {
    private var _binding: FragmentResearchRecordBinding? = null
    private val binding get() = _binding!!

    // view models
    private lateinit var recordViewModel: RecordsViewModel
    private lateinit var userViewModel: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResearchRecordBinding.inflate(layoutInflater, container, false)

        val view = binding.root


        val recycler = binding.researchRecordRecycler

        recordViewModel = ViewModelProvider(this).get(RecordsViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            recordViewModel.fetchRecord()
        }
        recordViewModel.recordList.observe(viewLifecycleOwner, Observer { recordList ->
            if (recordList != null) {
                val navController = findNavController()


                val sortedRecords = recordList.sortedBy { it.title }

                recycler.adapter = SearchRecordAdapter(
                    this@ResearchRecordFragment,
                    recordList,
                    R.layout.items_records,
                    viewLifecycleOwner,
                    navController,
                    userViewModel,
                )
            }
        })

        return view

    }
}