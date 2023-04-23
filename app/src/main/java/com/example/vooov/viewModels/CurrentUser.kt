package com.example.vooov.viewModels

import android.content.Context
import com.example.vooov.fragments.ConversationsFragment

class CurrentUser(context: Context){

    val sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

    val pseudo = sharedPreferences.getString("pseudo", "Anonymus" )
    val name = sharedPreferences.getString("name", "Anonyme" )
    val firstname = sharedPreferences.getString("firstname", "" )
    val email = sharedPreferences.getString("email", "email" )
    val phone = sharedPreferences.getString("phone", "0600000000" )
    val description = sharedPreferences.getString("description", "Un futur grand orateur?" )
    val url_profile_picture = sharedPreferences.getString("url_profile_picture", "" )
    val uuid = sharedPreferences.getString("uuid", "000" )
    val id = sharedPreferences.getInt("id", 0)
    var connected = sharedPreferences.getBoolean("userConnected", false )

    //allow to register a string value in sharedPreferences
    fun saveString(key: String, value: String) = sharedPreferences.edit().putString(key, value).apply()
    //allow to register an integer value in sharedPreferences
    fun saveInt(key: String, value: Int?) = value?.let {
        sharedPreferences.edit().putInt(key,
            it
        ).apply()
    }
    //allow to register a value in sharedPreferences. here if user is connected or not
    fun saveConnection(key: String, value: Boolean) = sharedPreferences.edit().putBoolean(key, value).apply()
    //allow to get back a string value from sharedPreferences
    fun readString(key: String) = sharedPreferences.getString(key, null)
    // Loging out the current user
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}