package com.example.beintouch.presentation


data class User @JvmOverloads constructor(
    val id: String = "",
    val email: String = "" ,
    val password: String = "",
    val name: String = "",
    val isOnline: Boolean = false
)

