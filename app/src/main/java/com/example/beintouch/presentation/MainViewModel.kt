package com.example.beintouch.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: AuthStateListener? = null
    private val database = Firebase.database
    private val users = database.getReference("Users")

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>>
        get() = _userList

    private val _messagesList = MutableLiveData<List<Message>>()
    val messagesList: LiveData<List<Message>>
        get() = _messagesList

    private val _companionUser = MutableLiveData<User>()
    val companionUser: LiveData<User>
        get() = _companionUser

    private val _isMessageSent = MutableLiveData<Boolean>()
    val isMessageSend: LiveData<Boolean>
        get() = _isMessageSent

    private val _isExistedUser = MutableLiveData<FirebaseUser>()
    val isExistedUser: LiveData<FirebaseUser>
        get() = _isExistedUser

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

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
        users.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfUsers = arrayListOf<User>()
                for (user in snapshot.children){
                    if (auth.currentUser?.uid != user.key){
                        val userFromDB = user.getValue(User::class.java)
                        if (userFromDB != null) {
                            listOfUsers.add(userFromDB)
                        }
                    }

                }
                _userList.value = listOfUsers
                Log.d("MainViewModel", listOfUsers.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MainViewModel", error.message)
            }

        })

    }


    fun signInWithEmailAndPassword(email: String, password: String) {
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

    fun signUpWithEmailAndPassword(email: String, password: String, full_name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userInfo = it.user

                val newUser = userInfo?.let { user ->
                    User(
                        user.uid,
                        email,
                        password,
                        full_name,
                        false
                    )
                }
                if (userInfo != null) {
                    users.child(userInfo.uid).setValue(newUser)
                }


            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _success.value = true
            }
            .addOnFailureListener {
                _error.value = it.message
            }

    }


    fun logout() {
        auth.signOut()
    }

}