package com.example.vooov

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import com.example.vooov.databinding.ActivityHomeBinding
import com.example.vooov.fragments.PlayBlocFragment
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.RecordsViewModel
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    // MainFragment actions
    private var mainFragmentOn : Boolean = false

    // The current selected record ID
    private var currentRecordId: Int = 1
    var selectedRecordFromRecycler: Int = -1

    // view models
    private lateinit var recordViewModel: RecordsViewModel
    private lateinit var userViewModel: UserViewModel

    // Play bloc
    private var mStartPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // To home fragment
        binding.homeMainHome.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
                supportFragmentManager.setFragmentResultListener(
                    "currentRecordId",
                    this@HomeActivity
                ) { _, result ->
                    val newRecordId = result.getInt("currentRecordId")
                    val toHomeFragment = Bundle()
                    toHomeFragment.putInt("mainFragment", newRecordId)
                    this@HomeActivity.findNavController(R.id.nav_host_fragment)
                        .navigate(R.id.homeFragment, toHomeFragment)
                    mainFragmentOn = true
                }
            }
        }


        // To personal profile fragment
        binding.homeMainProfil.setOnClickListener {
            // Check if user is signed in.
            if (!CurrentUser(this).connected) {
                LoginPopup(this).show()
            } else {
                this.findNavController(R.id.nav_host_fragment).navigate(R.id.personalProfileFragment)
                mainFragmentOn = false
            }
        }

        // To record list fragment
        binding.homeMainToRecycler.setOnClickListener{
            this.findNavController(R.id.nav_host_fragment).navigate(R.id.recordListFragment)
            mainFragmentOn = false
        }

        // To conversations list fragment
        binding.homeMainMessages.setOnClickListener{
            // Check if user is signed in.
            if (!CurrentUser(this).connected) {
                LoginPopup(this).show()
            } else {
                this.findNavController(R.id.nav_host_fragment).navigate(R.id.conversationsFragment)
                mainFragmentOn = false
            }
        }

        // To research record list fragment
        binding.homeMainResearch.setOnClickListener{
            this.findNavController(R.id.nav_host_fragment).navigate(R.id.searchRecordFragment)
            mainFragmentOn = false
        }
        val currentUserId = CurrentUser(this).readString("uuid")

        binding.homeMainOptions.setOnClickListener {
            CurrentUser(this).logout()
        }

    }
        override fun onStart() {
            super.onStart()
        mainFragmentOn = true
        // Playing bloc. Here we manage to find targeted record

            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val myFragment = PlayBlocFragment()
            fragmentTransaction.replace(R.id.play_bloc_fragment_container, myFragment)
            fragmentTransaction.commit()

        /*recordViewModel = ViewModelProvider(this).get(RecordsViewModel::class.java)
            userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

            // Highlight the selected audioRecord from the recycler
            selectedRecordFromRecycler= intent.getIntExtra("selectedRecord", -1)
            if(selectedRecordFromRecycler != -1){
                currentRecordId = selectedRecordFromRecycler
                Log.i(ContentValues.TAG, currentRecordId.toString())

            }


            CoroutineScope(Dispatchers.Main).launch {
            recordViewModel.fetchOneRecord(currentRecordId)
        }

        recordViewModel.record.observe(this, Observer { record ->
            if(record != null) {
                binding.homeMainRecordTitle.text = record.title
                val playButton : ImageButton = binding.homeMainRecordPlay

                playButton.setOnClickListener {

                    CoroutineScope(Dispatchers.Main).launch {
                        RecordManager().onPlay(mStartPlaying, record.uuid, this@HomeActivity)
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
                            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, arguments)
                        }
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    userViewModel.fetchOneUser(record.artist_uuid)

                }
                userViewModel.user.observe(this, Observer  { user ->
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

                CoroutineScope(Dispatchers.Main).launch {
                    recordViewModel.fetchOneRecord(currentRecordId)
                }

                recordViewModel.record.observe(this, Observer { record ->
                    if(record != null) {
                        binding.homeMainRecordTitle.text = record.title

                        if(mainFragmentOn){
                            val arguments = Bundle()
                            arguments.putInt("mainFragment", currentRecordId)
                            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, arguments)
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            userViewModel.fetchOneUser(record.artist_uuid)

                        }
                        userViewModel.user.observe(this, Observer  { user ->
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

                CoroutineScope(Dispatchers.Main).launch {
                    recordViewModel.fetchOneRecord(currentRecordId)
                }

                recordViewModel.record.observe(this, Observer { record ->
                    if(record != null) {
                        binding.homeMainRecordTitle.text = record.title

                        if(mainFragmentOn){
                            val arguments = Bundle()
                            arguments.putInt("mainFragment", currentRecordId)
                            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment, arguments)
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            userViewModel.fetchOneUser(record.artist_uuid)

                        }
                        userViewModel.user.observe(this, Observer  { user ->
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
            }*/




    }

}