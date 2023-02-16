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
import com.example.vooov.databinding.FragmentArtistProfileBinding
import com.example.vooov.databinding.FragmentWalletBinding
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArtistProfileFragment (
): Fragment() {
    private var _binding: FragmentArtistProfileBinding? = null
    private val binding get() = _binding!!

    // view models
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArtistProfileBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val currentUserUuid: String = arguments?.getString("toArtistProfileFragment")!!


        val artistProfileName = binding.artistProfilName
        val artistProfileNumberOfFriends = binding.artistProfilNumberOfFriends


        CoroutineScope(Dispatchers.Main).launch {
            userViewModel.fetchOneUser(currentUserUuid)
        }

        userViewModel.user.observe(viewLifecycleOwner, Observer{ user ->
            artistProfileName.text = user.pseudo
            artistProfileNumberOfFriends.text = user.number_of_friends.toString()
        })

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