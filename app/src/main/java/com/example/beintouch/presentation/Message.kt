package com.example.beintouch.presentation

data class Message @JvmOverloads constructor(
    val textMessage: String = "",
    val senderID: String = "",
    val companionID: String = "",
)