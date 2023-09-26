package com.example.beintouch.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModelFactory(
    private val currentUserID: String,
    private val compID: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(currentUserID, compID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}