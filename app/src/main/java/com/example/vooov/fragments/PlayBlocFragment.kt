package com.example.vooov.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.vooov.LoginPopup
import com.example.vooov.R
import com.example.vooov.StudioActivity
import com.example.vooov.databinding.FragmentPlayBlocBinding
import com.example.vooov.utilityClasses.login.PushedButtonView
import com.example.vooov.utilityClasses.login.RecordManager
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayBlocFragment: Fragment() {
    private var _binding: FragmentPlayBlocBinding? = null
    private val binding get() = _binding!!

    // view models
    private lateinit var userViewModel: UserViewModel
    private lateinit var recordViewModel: RecordsViewModel

    // The current selected record ID
    var currentRecordId: Int = 1
    private var selectedRecordFromRecycler: Int = -1
    var newCurrentRecord = 0
    val mainFragmentOn = true

    // Play bloc
    private var mStartPlaying = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayBlocBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        recordViewModel = ViewModelProvider(this).get(RecordsViewModel::class.java)




        // To other activities

        binding.homeMainRecordRecord.setOnClickListener {
            // Check if user is signed in.
            if (!CurrentUser(requireContext()).connected) {
                LoginPopup(requireContext()).show()
            } else {
                startActivity(Intent(requireContext(), StudioActivity::class.java))
            }
        }

        //Playing bloc interactions

        // Highlight the selected audioRecord from the recycler
        selectedRecordFromRecycler = if (arguments?.containsKey("selectedRecord") == true) {
            requireArguments().getInt("selectedRecord")
        } else {
            -1
        }
        if(selectedRecordFromRecycler != -1){
            currentRecordId = selectedRecordFromRecycler
            Log.i(ContentValues.TAG, currentRecordId.toString())
        }


        CoroutineScope(Dispatchers.Main).launch {
            recordViewModel.fetchOneRecord(currentRecordId)
        }

        recordViewModel.record.observe(viewLifecycleOwner, Observer { record ->
            if(record != null) {
                binding.homeMainRecordTitle.text = record.title
                val playButton : ImageButton = binding.homeMainRecordPlay

                playButton.setOnClickListener {

                    CoroutineScope(Dispatchers.Main).launch {
                        RecordManager().onPlay(mStartPlaying, record.uuid, requireContext())
                        PushedButtonView().changeImageButtonOnPush(
                            playButton,
                            mStartPlaying,
                            R.drawable.ic_play,
                            R.drawable.ic_pause
                        )
                        mStartPlaying = !mStartPlaying

                        if(mainFragmentOn){
                            val arguments = Bundle()
                            arguments.putInt("mainFragment", currentRecordId)
                            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, arguments)
                        }
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    userViewModel.fetchOneUser(record.artist_uuid)

                }
                userViewModel.user.observe(viewLifecycleOwner, Observer  { user ->
                    if(user !=null) {
                        binding.homeMainRecordArtistName.text = user.pseudo
                    } else {
                        Log.i(ContentValues.TAG, "user null")

                    }
                })
            } else {
                Log.i(ContentValues.TAG, "record null")

            }

        })

        // To the next record
        binding.homeMainRecordForward.setOnClickListener {
            currentRecordId++

            //To share current record id with home activity
            val toHomeActivity = Bundle()
            toHomeActivity.putInt("currentRecordId", currentRecordId)
            parentFragmentManager.setFragmentResult("currentRecordId", toHomeActivity)

            CoroutineScope(Dispatchers.Main).launch {
                recordViewModel.fetchOneRecord(currentRecordId)
            }

            recordViewModel.record.observe(viewLifecycleOwner, Observer { record ->
                if(record != null) {
                    binding.homeMainRecordTitle.text = record.title

                    if(mainFragmentOn){
                        val arguments = Bundle()
                        arguments.putInt("mainFragment", currentRecordId)
                        requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, arguments)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        userViewModel.fetchOneUser(record.artist_uuid)

                    }
                    userViewModel.user.observe(viewLifecycleOwner, Observer  { user ->
                        if(user !=null) {
                            binding.homeMainRecordArtistName.text = user.pseudo
                        } else {
                            Log.i(ContentValues.TAG, "user null")

                        }
                    })
                } else {
                    Log.i(ContentValues.TAG, "record null")

                }

            })
        }

        // To the previous record
        binding.homeMainRecordRewind.setOnClickListener {
            currentRecordId--

            //To share current record id with home activity
            val toHomeActivity = Bundle()
            toHomeActivity.putInt("currentRecordId", currentRecordId)
            parentFragmentManager.setFragmentResult("currentRecordId", toHomeActivity)

            CoroutineScope(Dispatchers.Main).launch {
                recordViewModel.fetchOneRecord(currentRecordId)
            }

            recordViewModel.record.observe(viewLifecycleOwner, Observer { record ->
                if(record != null) {
                    binding.homeMainRecordTitle.text = record.title

                    if(mainFragmentOn){
                        val arguments = Bundle()
                        arguments.putInt("mainFragment", currentRecordId)
                        requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, arguments)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        userViewModel.fetchOneUser(record.artist_uuid)

                    }
                    userViewModel.user.observe(viewLifecycleOwner, Observer  { user ->
                        if(user !=null) {
                            binding.homeMainRecordArtistName.text = user.pseudo
                        } else {
                            Log.i(ContentValues.TAG, "user null")

                        }
                    })
                } else {
                    Log.i(ContentValues.TAG, "record null")

                }

            })
        }

        return view
    }
}