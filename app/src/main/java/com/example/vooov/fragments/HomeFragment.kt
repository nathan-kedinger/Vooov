package com.example.vooov.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.vooov.HomeActivity
import com.example.vooov.R
import com.example.vooov.databinding.FragmentHomeBinding
import com.example.vooov.databinding.FragmentWalletBinding
import com.example.vooov.utilityClasses.login.PushedButtonView
import com.example.vooov.utilityClasses.login.RecordManager
import com.example.vooov.viewModels.ConversationsViewModel
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment (
): Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // view models
    private lateinit var recordViewModel: RecordsViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var conversationViewModel: ConversationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        // ViewModels calls
        recordViewModel = ViewModelProvider(this).get(RecordsViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        conversationViewModel = ViewModelProvider(this).get(ConversationsViewModel::class.java)

        val currentRecordId: Int = arguments?.getInt("mainFragment")!!

        val artistName = binding.homeRecordArtistName
        val recordTitle = binding.homeRecordTitle
        val recordKind = binding.homeRecordKind
        val recordVoiceStyle = binding.homeRecordVoiceStyle
        val recordNumberOfPlay = binding.homeRecordNumberOfPlay
        val recordNumberOfMoons = binding.homeRecordNumberOfMoons
        val recordDescription = binding.homeRecordDescription

        // To other fragments
        CoroutineScope(Dispatchers.Main).launch {
            recordViewModel.fetchOneRecord(currentRecordId)
        }

        recordViewModel.record.observe(viewLifecycleOwner, Observer { record ->
            if(record != null) {
                recordTitle.text = record.title
                recordKind.text = record.kind
                recordVoiceStyle.text = record.voice_style
                recordNumberOfPlay.text = record.number_of_plays.toString()
                recordNumberOfMoons.text = record.number_of_moons.toString()
                recordDescription.text = record.description


                val context = requireContext()

                CoroutineScope(Dispatchers.Main).launch {
                    userViewModel.fetchOneUser(record.artist_uuid)
                }

                userViewModel.user.observe(viewLifecycleOwner, Observer  { user ->
                    if(user !=null) {
                        artistName.text = user.pseudo
                        if(CurrentUser(context).readString("uuid") != "000"){
                            binding.homeRecordSendMessage.setOnClickListener{
                                CoroutineScope(Dispatchers.Main).launch {
                                    conversationViewModel.fetchOneConversation(
                                        user.uuid,
                                        CurrentUser(context).readString("uuid")!!
                                    )
                                    // Attendre que la conversation soit récupérée
                                    conversationViewModel.conversation.observe(
                                        viewLifecycleOwner,
                                        Observer { conversation ->
                                            if (conversation != null) {
                                                // Si une conversation existe déjà, ouvrir la page de messages
                                                val toSendMessageArgs = Bundle()
                                                toSendMessageArgs.putString(
                                                    "toSendMessageFragment",
                                                    conversation.uuid
                                                )
                                                toSendMessageArgs.putString(
                                                    "toSendMessageContactUuid",
                                                    user.uuid
                                                )
                                                findNavController().navigate(
                                                    R.id.action_homeFragment_to_messageFragment,
                                                    toSendMessageArgs
                                                )
                                            } else {
                                                // Sinon, créer une nouvelle conversation
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    conversationViewModel.createConversation(
                                                        user.uuid,
                                                        CurrentUser(context).readString("uuid")!!
                                                    )
                                                    // Ouvrir la page de messages
                                                    val toSendMessageArgs = Bundle()
                                                    toSendMessageArgs.putString(
                                                        "toSendMessageFragment",
                                                        user.uuid
                                                    )
                                                    findNavController().navigate(
                                                        R.id.action_homeFragment_to_messageFragment,
                                                        toSendMessageArgs
                                                    )
                                                }
                                            }
                                        })
                                }
                            }

                            binding.homeRecordPlus.setOnClickListener {
                                val toRecordPageFragment = Bundle()
                                toRecordPageFragment.putInt("toRecordPageFragment", currentRecordId)
                                // Adding argument to get to the good record here
                                findNavController().navigate(R.id.action_homeFragment_to_recordPageFragment, toRecordPageFragment)
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


        return view

    }
}