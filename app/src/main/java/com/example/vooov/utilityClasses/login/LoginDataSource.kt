package com.example.vooov.utilityClasses.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.vooov.LoginActivity
import com.example.vooov.data.dataInterfaces.UsersRetrofitInterface
import com.example.vooov.data.model.LoggedInUser
import com.example.vooov.data.model.UserModel
import com.example.vooov.repositories.UserRepository
import com.example.vooov.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val User = LoggedInUser("1","couille" )

            return Result.Success(User)

        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}