package com.example.beintouch.presentation

import com.google.firebase.auth.FirebaseUser

interface AuthStateListener {
    fun onUserAuthenticated(user: FirebaseUser?)
    fun onUserUnauthenticated()
}