package com.example.vooov.data.model

data class UserModel(
    val uuid: String = "null",
    val name: String = "Doe",
    val firstname: String = "Jon",
    val email: String = "a@gt.fr",
    val password: String = "pass",
    val birthday: String = "01/01/2023",
    val phone: String = "0600000000",
    val description: String = "",
    var number_of_followers: Int? = 0,
    var number_of_moons: Int? = 0,
    var number_of_friends: Int? = 0,
    val url_profile_picture: String = "null",
    val sign_in: String = "",
    val last_connection: String = ""
)
