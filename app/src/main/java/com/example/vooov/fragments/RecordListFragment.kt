package com.example.vooov.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.vooov.HomeActivity
import com.example.vooov.R
import com.example.vooov.adapters.RecordAdapter
import com.example.vooov.databinding.FragmentRecordListBinding
import com.example.vooov.databinding.FragmentWalletBinding
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordListFragment: Fragment(), RecordAdapter.RecordAdapterListener {
    private var _binding: FragmentRecordListBinding? = null
    private val binding get() =_binding!!

    // view models
    private lateinit var recordViewModel: RecordsViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordListBinding.inflate(layoutInflater, container, false)

        val view = binding.root
        val recycler = binding.recordsHomeRecycler

        recordViewModel = ViewModelProvider(this).get(RecordsViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            recordViewModel.fetchRecord()
        }
        recordViewModel.recordList.observe(viewLifecycleOwner, Observer { recordList ->
            if (recordList != null) {
                val navController = findNavController()

                recycler.adapter = RecordAdapter(
                    this@RecordListFragment,
                    recordList,
                    R.layout.items_records,
                    viewLifecycleOwner,
                    navController,
                    userViewModel,
                    this@RecordListFragment
                )
            }
        })
        // To other fragments

        return view

    }

    override fun onRecordItemSelected(recordId: Int) {


// Obtenir le gestionnaire de fragments
        /*val fragmentManager = parentFragmentManager

// Créer une instance du fragment de destination
        val destinationFragment = fragmentManager.findFragmentById(R.id.play_bloc_fragment) as PlayBlocFragment*/

        val toPlayBlocFragment = Bundle()
        toPlayBlocFragment.putInt("selectedRecord", recordId)
        PlayBlocFragment().arguments = toPlayBlocFragment
// Commencer la transaction de fragments
        /*val fragmentTransaction = fragmentManager.beginTransaction()

// Ajouter le fragment de destination à la transaction
        fragmentTransaction.add(R.id.play_bloc_fragment, destinationFragment)

// Ajouter la transaction à la pile de fragments (optionnel)
        fragmentTransaction.addToBackStack(null)

// Terminer la transaction
        fragmentTransaction
            .replace(R.id.play_bloc_fragment, destinationFragment)
            .commit()*/

        val transaction = childFragmentManager.beginTransaction() //manipulation des fragments
        transaction.replace(R.id.play_bloc_fragment_container,PlayBlocFragment())// on remplace le fragment
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
