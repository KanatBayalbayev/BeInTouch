package com.example.beintouch.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: AuthStateListener? = null


    private val _isExistedUser = MutableLiveData<FirebaseUser>()
    val isExistedUser: LiveData<FirebaseUser>
        get() = _isExistedUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun setAuthStateListener(listener: AuthStateListener) {
        authStateListener = listener
        auth.addAuthStateListener {
            val user = it.currentUser
            if (user != null) {
                authStateListener?.onUserAuthenticated(user)
            } else {
                authStateListener?.onUserUnauthenticated()
            }
        }
    }



    fun signInWithEmailAndPassword(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _isExistedUser.value = it.user
                Log.d("LoginViewModel", "Success: $it")
            }
            .addOnFailureListener {
                _error.value = it.message
                Log.d("LoginViewModel", "Error: $it")
            }

    }
//    auth.addAuthStateListener {
//        if (it.currentUser != null) {
//            _isExistedUser.value = it.currentUser
//        }
//    }
}