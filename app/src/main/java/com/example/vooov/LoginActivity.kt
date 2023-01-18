package com.example.vooov

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.vooov.data.model.LoggedInUser
import com.example.vooov.databinding.ActivityLoginBinding

import com.example.vooov.data.model.LoggedInUserView
import com.example.vooov.viewModels.LoginViewModel
import com.example.vooov.viewModelFactories.LoginViewModelFactory
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        val sharedPreferences = this.getSharedPreferences("userSahredPreferences", Context.MODE_PRIVATE)
        val preferencesEditor = sharedPreferences.edit()

        val getCurrentUserConnected = sharedPreferences.getBoolean("userConnected", false)
        val getCurrentUserName = sharedPreferences.getString("name", "Anonyme" )

        if ( getCurrentUserConnected == true){
            login.isEnabled
            Toast.makeText(this, getCurrentUserName, Toast.LENGTH_LONG).show()
            login.setOnClickListener{
                preferencesEditor.putBoolean("userConnected", false)

            }
        } else {

            loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

            userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

            loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                login.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    username.error = getString(loginState.usernameError)
                }
                if (loginState.passwordError != null) {
                    password.error = getString(loginState.passwordError)
                }
            })

            loginViewModel.loginResult.observe(this@LoginActivity, Observer {
                val loginResult = it ?: return@Observer

                loading.visibility = View.GONE
                if (loginResult.error != null) {
                    showLoginFailed(loginResult.error)
                }
                if (loginResult.success != null) {
                    updateUiWithUser(loginResult.success)
                    Toast.makeText(this, "connecté", Toast.LENGTH_LONG).show()
                }
                setResult(Activity.RESULT_OK)

                //Complete and destroy login activity once successful
                //finish()
            })

            username.afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }




            password.apply {
                afterTextChanged {
                    loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                    )
                }


                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                            )
                    }
                    false
                }
            }

            login.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    userViewModel.fetchOneUser(username.text.toString())

                }

                userViewModel.user.observe(this@LoginActivity, Observer { user ->

                    if (user != null) {
                        if (user.password == password.text.toString()) {
                            preferencesEditor.putString("email", user.email)
                            preferencesEditor.putString("name", user.name)
                            preferencesEditor.putString("firstname", user.firstname)
                            preferencesEditor.putString("phone", user.phone)
                            preferencesEditor.putString("description", user.description)
                            preferencesEditor.putString("url_profile_picture", user.url_profile_picture)
                            preferencesEditor.putString("uuid", user.uuid)
                            preferencesEditor.putBoolean("userConnected", true)
                            preferencesEditor.apply()

                            loading.visibility = View.VISIBLE
                            loginViewModel.login(username.text.toString(), password.text.toString())
                            Toast.makeText(applicationContext, user.toString(), Toast.LENGTH_LONG).show()

                            Toast.makeText(applicationContext, "ça marche", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "pas bon", Toast.LENGTH_LONG).show()

                    }
                })
            }


        }

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}