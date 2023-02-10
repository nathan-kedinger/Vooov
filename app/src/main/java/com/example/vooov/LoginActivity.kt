package com.example.vooov

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import com.example.vooov.viewModels.CurrentUser
import com.example.vooov.viewModels.UserViewModel
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import de.mkammerer.argon2.Argon2Factory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import org.mindrot.jbcrypt.BCrypt
import java.io.IOException

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
        val getCurrentUserName = sharedPreferences.getString("name", "null")

        /*if ( getCurrentUserConnected != false && getCurrentUserConnected == false ){
            login.isEnabled
            Toast.makeText(this, getCurrentUserName, Toast.LENGTH_LONG).show()

        } else {*/

            loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

            userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

           /* loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                login.isEnabled = loginState.isDataValid

                if (loginState.usernameError != null) {
                    username.error = getString(loginState.usernameError)
                }
                if (loginState.passwordError != null) {
                    password.error = getString(loginState.passwordError)
                }
            })*/

            /*loginViewModel.loginResult.observe(this@LoginActivity, Observer {
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
            }*/

            try {
                login.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        userViewModel.fetchOneUser(username.text.toString())
                        Log.i(ContentValues.TAG, username.text.toString())

                    }

                    userViewModel.user.observe(this@LoginActivity, Observer {
                        val user = it ?:return@Observer

                        if (user.password != null) {

                            val argon2Kt = Argon2Kt()
                            val verificationResult: Boolean = argon2Kt.verify(Argon2Mode.ARGON2_I, user.password, password.text.toString().toByteArray())
                            if (verificationResult) {
                                // Code pour enregistrer les informations de l'utilisateur dans les préférences
                                CurrentUser(this).saveString("email", user.email)
                                CurrentUser(this).saveString("pseudo", user.pseudo)
                                CurrentUser(this).saveString("name", user.name)
                                CurrentUser(this).saveString("firstname", user.firstname)
                                CurrentUser(this).saveString("phone", user.phone)
                                CurrentUser(this).saveString("description", user.description)
                                CurrentUser(this).saveString("url_profile_ picture", user.url_profile_picture)
                                CurrentUser(this).saveString("uuid", user.uuid)
                                CurrentUser(this).saveConnection("userConnected", true)


                                // Code pour démarrer une nouvelle activité et afficher un message de confirmation
                                startActivity(Intent(this, HomeActivity::class.java))
                                //loading.visibility = View.VISIBLE
                                //loginViewModel.login(username.text.toString(), password.text.toString())
                                Toast.makeText(applicationContext, "Connexion réussie!", Toast.LENGTH_LONG).show()
                            } else {
                                // Afficher un message d'erreur si la vérification échoue
                                Toast.makeText(applicationContext, "Identifiants incorrects!", Toast.LENGTH_LONG).show()
                            }
                            Toast.makeText(applicationContext, "Utilisateur !", Toast.LENGTH_LONG).show()

                        } else {
                            // Afficher un message d'erreur si l'utilisateur n'est pas trouvé
                            Toast.makeText(applicationContext, "Utilisateur non trouvé!", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "Message: ${e.message}")
            }

        //}

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
}

