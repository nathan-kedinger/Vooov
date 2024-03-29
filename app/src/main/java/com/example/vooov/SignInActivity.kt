package com.example.vooov

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import com.example.vooov.data.model.UserModel
import com.example.vooov.databinding.ActivitySignInBinding
import com.example.vooov.viewModelFactories.LoginViewModelFactory
import com.example.vooov.viewModels.LoginViewModel
import com.example.vooov.viewModels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.io.IOException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class SignInActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val signInButton = binding.registerRegister

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)



       /* loginViewModel.loginFormState.observe(this@SignInActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless all fields are valid
            signInButton.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })*/



        //CREATE USER
         signInButton.setOnClickListener {
            try {



                // Check for password confirmation
                if (binding.registerPasswordTextInput.text.toString() == binding.registerPasswordConfirmTextInput.text.toString()){



                    if (isEmailValid(binding.registerMailTextInput.text.toString())){
                        val actualDateTime = Date()

                        CoroutineScope(Dispatchers.Main).launch {
                            userViewModel.fetchOneUserByMail(binding.registerMailTextInput.text.toString())
                        }

                        userViewModel.userByMail.observe(this, androidx.lifecycle.Observer { user->
                            if (user.email != null || user != null) {

                                val snackbar: Snackbar = Snackbar.make(CoordinatorLayout(this),"Cette adresse email est déjà utilisée", Snackbar.LENGTH_LONG)
                                snackbar.show()

                            } else {
                                // Generate password salt and hash
                                val password = binding.registerPasswordTextInput.text.toString()

                               //Bcrypt
                                val salt =  BCrypt.gensalt()
                                val hash = BCrypt.hashpw(password, salt)

                                //argon2
                                /*val salt = SecureRandom.getInstanceStrong().generateSeed(16)
                                val argon2Kt = Argon2Kt()
                                val hashResult: Argon2KtResult = argon2Kt.hash(Argon2Mode.ARGON2_I, password.toByteArray(),salt,5, 65536)
                                val hash = hashResult.encodedOutputAsString()*/

                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val currentDate = dateFormat.format(Date())

                                val gson = Gson()
                                val roles = listOf("ROLE_USER")
                                val rolesJson = gson.toJson(roles)

                                // Création de l'utilisateur
                                val newUser = UserModel(
                                    null,
                                    binding.registerMailTextInput.text.toString(),
                                    rolesJson,
                                    hash,
                                    0,
                                    UUID.randomUUID().toString(),
                                    binding.registerNameTextInput.text.toString(),
                                    binding.registerPseudoTextInput.text.toString(),
                                    binding.registerFirstnameTextInput.text.toString(),
                                    currentDate,
                                    "",
                                    "",
                                    0,
                                    1000,
                                    0,
                                    "logo_vooov_small.png",
                                    currentDate,
                                    currentDate
                                )

                                userViewModel.createUser(newUser)
                                startActivity(Intent(this, LoginActivity::class.java))

                            }
                        })
                    } else {
                        throw IOException("Email doesn't match")
                    }
                } else{
                    throw IOException("Password doesn't match")
                }
            } catch (e: Exception){
                Log.i(ContentValues.TAG, "Message: ${e.message}")
            }
        }


    }
    fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

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
