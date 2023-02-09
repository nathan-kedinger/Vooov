package com.example.vooov.viewModels

import android.content.Context

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


fun saveString(key: String, value: String) = sharedPreferences.edit().putString(key, value).apply()
fun readString(key: String) = sharedPreferences.getString(key, null)
}