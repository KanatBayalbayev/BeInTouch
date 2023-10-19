package com.example.beintouch.presentation


data class User @JvmOverloads constructor(
    val id: String = "",
    val email: String = "",
    var password: String = "",
    var name: String = "",
    val online: Boolean = false,
    var userProfileImage: String = "",
    var selected: Boolean = false,
    var lastMessage: String = "",
    var lastTimeMessageSent: String = ""
)

