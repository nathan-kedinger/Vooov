package com.example.vooov.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vooov.R
import com.example.vooov.data.model.UserModel
import com.example.vooov.databinding.FragmentPersonalProfileBinding
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonalProfileFragment (
): Fragment() {
    private var _binding: FragmentPersonalProfileBinding? = null
    private val binding get() = _binding!!

    // view models
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalProfileBinding.inflate(layoutInflater, container, false)

        val view = binding.root

        val personalProfileMyName = binding.personalProfilMyName
        val personalProfileEmail = binding.personalProfilMail
        val personalProfilePseudo = binding.personalProfilPseudo
        val personalProfileName = binding.personalProfilName
        val personalProfileFirstname = binding.personalProfilFirstname
        val personalProfileBiography =binding.personalProfilBiographieText
        val context = requireContext()
        val currentUser = CurrentUser(requireContext()).readString("uuid")
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        // To other fragments

        binding.personalProfilCommunity.setOnClickListener {
            //  Adding arguments here to get the good conversation list
            findNavController().navigate(R.id.action_personalProfileFragment_to_communityFragment)
        }

        binding.personalProfilMyRecords.setOnClickListener {
            // Adding argumetns here to get the personal record list
            findNavController().navigate(R.id.action_personalProfileFragment_to_recyclerRecordFragment)
        }

        binding.personalProfilWallet.setOnClickListener {
            // Adding arguments here
            findNavController().navigate(R.id.action_personalProfileFragment_to_walletFragment)

        }

        CoroutineScope(Dispatchers.Main).launch {
            userViewModel.fetchOneUser(currentUser)
        }

        userViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            val userDescription = user.description
            personalProfileMyName.text = user.name
            personalProfileEmail.text = user.email
            personalProfilePseudo.text = user.pseudo
            personalProfileName.text = user.name
            personalProfileFirstname.text = user.firstname
            personalProfileBiography.setText(userDescription)


            binding.personalProfilModify.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val currentUserModel = UserModel(
                        null,
                        user.email.trim(),
                        "",
                        user.password.trim(),
                        0,
                        user.uuid.trim(),
                        user.pseudo.trim(),
                        user.name.trim(),
                        user.firstname.trim(),
                        user.birthday.trim(),
                        user.phone.trim(),
                        binding.personalProfilBiographieText.text.toString(),
                        user.number_of_followers,
                        user.number_of_moons,
                        user.number_of_friends,
                        user.url_profile_picture.trim(),
                        user.sign_in.trim(),
                        user.last_connection.trim()
                    )
                    Log.i(ContentValues.TAG, currentUserModel.toString())

                    userViewModel.updateUser(currentUser, currentUserModel)
                }
            }
        })

        return view
    }
}