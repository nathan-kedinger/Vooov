package com.example.vooov.fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
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
import com.example.vooov.data.model.RecordModel
import com.example.vooov.data.model.UserModel
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

        // UI ProgressBar
        val progressBar = binding.recordPageProgressbar
        val textViewsToHide = arrayListOf(recordArtistName, recordTitle, recordKind, recordVoiceStyle, recordCreatedAt, recordDescription)
        progressBar.visibility = View.VISIBLE
        for (textView in textViewsToHide){
            textView.visibility = View.GONE
        }

        CoroutineScope(Dispatchers.Main).launch {
            recordViewModel.fetchOneRecord(currentRecordId)
        }

        // Bind record data to view
        recordViewModel.record.observe(viewLifecycleOwner, Observer { record ->
            if(record != null) {
                recordTitle.text = record.title
                recordKind.text = record.categories_id.toString()
                recordVoiceStyle.text = record.voice_style_id.toString()
                recordCreatedAt.text = record.created_at
                recordNumberOfPlays.text = record.number_of_plays.toString()
                recordNumberOfMoons.text = record.number_of_moons.toString()
                recordDescription.text = record.description

                val context = requireContext()

                CoroutineScope(Dispatchers.Main).launch {
                    userViewModel.fetchOneUserById(record.artist_id)
                    // UI ProgressBar
                    progressBar.visibility = View.GONE
                    for (textView in textViewsToHide) {
                        textView.visibility = View.VISIBLE
                    }
                }

                userViewModel.userById.observe(viewLifecycleOwner, Observer  { user ->
                    Log.i(ContentValues.TAG, "targeted user : ${user.id}")
                    if(user !=null) {
                        recordArtistName.text = user.pseudo
                        Log.i(ContentValues.TAG, "Is user connected : ${CurrentUser(context).connected}")
                        if(CurrentUser(context).connected){
                            Log.i(ContentValues.TAG, "targeted user : ${user.id}")

                            //Sending Moons
                            binding.recordPageSendMoon.setOnClickListener {
                                val amountOfMoonsToSend = binding.recordPageMoonEditText.text.toString().toInt()
                                val currentUserUuid = CurrentUser(context).readString("uuid")
                                CoroutineScope(Dispatchers.Main).launch {
                                    userViewModel.fetchOneUser(currentUserUuid)
                                }

                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Valider l'envoi de moons")
                                builder.setMessage("Etes-vous sûre de vouloir envoyer ces moons?")

                                builder.setPositiveButton("Valider", DialogInterface.OnClickListener { dialog, which ->
                                    // Updating profiles
                                    userViewModel.user.observe(viewLifecycleOwner, Observer { currentUser ->
                                        if (currentUser != null){

                                            var currentUserNumberOfMoons = currentUser.number_of_moons
                                            var otherUserNumberOfMoons = user.number_of_moons
                                            var recordNumberOfMoons = record.number_of_moons

                                            if (currentUserNumberOfMoons > amountOfMoonsToSend){

                                                currentUserNumberOfMoons -= amountOfMoonsToSend
                                                otherUserNumberOfMoons += amountOfMoonsToSend
                                                recordNumberOfMoons += amountOfMoonsToSend

                                                val currentUserModel = UserModel(
                                                    null,
                                                    currentUser.email,
                                                    currentUser.roles,
                                                    currentUser.password,
                                                    currentUser.is_verified,
                                                    currentUser.uuid,
                                                    currentUser.pseudo,
                                                    currentUser.name,
                                                    currentUser.firstname,
                                                    currentUser.birthday,
                                                    currentUser.phone,
                                                    currentUser.description,
                                                    currentUser.number_of_followers,
                                                    currentUserNumberOfMoons,
                                                    currentUser.number_of_friends,
                                                    currentUser.url_profile_picture,
                                                    currentUser.sign_in,
                                                    currentUser.last_connection
                                                )

                                                val otherUserModel = UserModel(
                                                    null,
                                                    currentUser.email,
                                                    currentUser.roles,
                                                    currentUser.password,
                                                    currentUser.is_verified,
                                                    currentUser.uuid,
                                                    currentUser.pseudo,
                                                    currentUser.name,
                                                    currentUser.firstname,
                                                    currentUser.birthday,
                                                    currentUser.phone,
                                                    currentUser.description,
                                                    currentUser.number_of_followers,
                                                    otherUserNumberOfMoons,
                                                    currentUser.number_of_friends,
                                                    currentUser.url_profile_picture,
                                                    currentUser.sign_in,
                                                    currentUser.last_connection
                                                )

                                                val recordModel = RecordModel(
                                                    record.id,
                                                    record.artist_id,
                                                    record.categories_id,
                                                    record.voice_style_id,
                                                    record.uuid,
                                                    record.title,
                                                    record.length,
                                                    record.number_of_plays,
                                                    recordNumberOfMoons,
                                                    record.description,
                                                    record.created_at,
                                                    record.updated_at
                                                )

                                                CoroutineScope(Dispatchers.Main).launch {
                                                    userViewModel.updateUser(currentUser.uuid, currentUserModel)
                                                    userViewModel.updateUser(user.uuid, otherUserModel)
                                                    recordViewModel.updateRecord(record.uuid, recordModel)
                                                }

                                                val toRecordFragmentSelf = Bundle()
                                                toRecordFragmentSelf.putInt("toRecordPageFragment", currentRecordId)
                                                findNavController().navigate(R.id.action_recordPageFragment_self, toRecordFragmentSelf)
                                                Toast.makeText(context, "Vous avez envoyé $amountOfMoonsToSend à ${user.pseudo}", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(activity,
                                                    "Vous n'avez pas assez de moons", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    })

                                })

                                builder.setNegativeButton("Annuler", DialogInterface.OnClickListener { dialog, which ->
                                    Toast.makeText(context, "Vous avez cliqué sur le bouton Non", Toast.LENGTH_SHORT).show()
                                })

                                builder.show()
                            }

                            // To Artist profile fragment with targeted user uuid
                            binding.recordPageGoToArtist.setOnClickListener{
                                Log.i(ContentValues.TAG, "recordPageGoToArtist clicked")
                                val toArtistProfileArgs = Bundle()
                                toArtistProfileArgs.putInt("toArtistProfileFragment", user.id!!)
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
/*
        binding.recordPageGoToArtist.setOnClickListener {
            // Adding identifications arguments here

            findNavController().navigate(R.id.action_recordPageFragment_to_artistProfilFragment)

        }*/

        return view

    }
}