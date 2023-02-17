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
                                                    currentUser.uuid,
                                                    currentUser.pseudo,
                                                    currentUser.name,
                                                    currentUser.firstname,
                                                    currentUser.email,
                                                    currentUser.password,
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
                                                    currentUser.uuid,
                                                    currentUser.pseudo,
                                                    currentUser.name,
                                                    currentUser.firstname,
                                                    currentUser.email,
                                                    currentUser.password,
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
                                                    record.uuid,
                                                    record.id,
                                                    record.artist_uuid,
                                                    record.title,
                                                    record.length,
                                                    record.number_of_plays,
                                                    recordNumberOfMoons,
                                                    record.voice_style,
                                                    record.kind,
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