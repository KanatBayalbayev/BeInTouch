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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: AuthStateListener? = null
    private val database = Firebase.database
    private val users = database.getReference("Users")


    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>>
        get() = _userList

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun currentUser(email: String) {
        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfUsers = arrayListOf<User>()
                for (userDb in snapshot.children) {
                    val userEmail = userDb.child("email").getValue(String::class.java)
                    if (email == userEmail) {
                        val userFromDB = userDb.getValue(User::class.java)
                        if (userFromDB != null) {
                            listOfUsers.add(userFromDB)
                        }
                    }
                }
                _userList.value = listOfUsers
                Log.d("New Users", listOfUsers.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("New Users", error.toString())
            }

        })
    }

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
        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfUsers = arrayListOf<User>()

                for (user in snapshot.children) {

//                    if (auth.currentUser?.uid != user.key) {
//                        val userFromDB = user.getValue(User::class.java)
//                        if (userFromDB != null) {
//                            listOfUsers.add(userFromDB)
//                        }
//                    }
                    if (auth.currentUser?.uid == user.key) {
                        val userFromDB = user.getValue(User::class.java)
                        if (userFromDB != null) {
                            _userName.value = userFromDB.name
                        }
                    }
                }
//                _userList.value = listOfUsers
                Log.d("MainViewModel", "USERS: $listOfUsers")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MainViewModel", error.message)
            }
        })
    }

    fun setUserOnline(isOnline: Boolean) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            users.child(userId).child("online").setValue(isOnline)
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }

    fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        full_name: String,
        isOnline: Boolean
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userInfo = it.user

                val newUser = userInfo?.let { user ->
                    User(
                        user.uid,
                        email,
                        password,
                        full_name,
                        isOnline
                    )
                }
                if (newUser != null) {
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
        setUserOnline(false)
        auth.signOut()
    }

}