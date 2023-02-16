package com.example.vooov.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.databinding.FragmentRecordBinding
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordFragment (
): Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    // view models
    private lateinit var recordViewModel: RecordsViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        recordViewModel = ViewModelProvider(this).get(RecordsViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Fragment arguments
        val currentRecordId: Int = arguments?.getInt("toRecordPageFragment")!!
        Log.i(ContentValues.TAG, currentRecordId.toString())

        // Variables
        val recordArtistName = binding.recordPageArtistName
        val recordTitle = binding.recordPageRecordTitle
        val recordKind = binding.recordPageKind
        val recordVoiceStyle = binding.recordPageVoiceStyle
        val recordCreatedAt = binding.recordPageRecordDate
        val recordNumberOfPlays = binding.recordPageNumberOfPlay
        val recordNumberOfMoons = binding.recordPageNumberOfMoon
        val recordDescription = binding.recordPageRecordDescription

        CoroutineScope(Dispatchers.Main).launch {
            recordViewModel.fetchOneRecord(currentRecordId)
        }

        recordViewModel.record.observe(viewLifecycleOwner, Observer { record ->
            if(record != null) {
                recordTitle.text = record.title
                recordKind.text = record.kind
                recordVoiceStyle.text = record.voice_style
                recordCreatedAt.text = record.created_at
                recordNumberOfPlays.text = record.number_of_plays.toString()
                recordNumberOfMoons.text = record.number_of_moons.toString()
                recordDescription.text = record.description

                val context = requireContext()

                CoroutineScope(Dispatchers.Main).launch {
                    userViewModel.fetchOneUser(record.artist_uuid)
                }

                userViewModel.user.observe(viewLifecycleOwner, Observer  { user ->
                    if(user !=null) {
                        recordArtistName.text = user.pseudo
                        if(CurrentUser(context).readString("userConnecterd") != "000"){

                            binding.recordPageGoToArtist.setOnClickListener{
                                val toArtistProfileArgs = Bundle()
                                toArtistProfileArgs.putString("toArtistProfileFragment", user.uuid )
                                // Adding identifications and conversations crations code here
                                findNavController().navigate(R.id.action_recordPageFragment_to_artistProfilFragment, toArtistProfileArgs)
                            }

                        } else {
                            Toast.makeText(activity, "Vous n'êtes pas connecté", Toast.LENGTH_LONG).show()
                            //POPUP de connection
                        }

                    } else {
                        Log.i(ContentValues.TAG, "user null")

                    }
                })
            } else {
                Log.i(ContentValues.TAG, "record null")

            }

        })

        // To other fragments

        binding.recordPageGoToArtist.setOnClickListener {
            // Adding identifications arguments here

            findNavController().navigate(R.id.action_recordPageFragment_to_artistProfilFragment)

        }

        return view

    }
}