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

class MainViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: AuthStateListener? = null
    private val database = Firebase.database
    private val users = database.getReference("Users")

    // TODO new impl
    private val messages = database.getReference("Messages")


    private val _userID = MutableLiveData<String?>()
    val userID: LiveData<String?>
        get() = _userID
    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName
    private val _isUserOnline= MutableLiveData<Boolean?>()
    val isUserOnline: LiveData<Boolean?>
        get() = _isUserOnline

    val _companionID = MutableLiveData<String>()
    val companionID: LiveData<String?>
        get() = _companionID

    val companionName = MutableLiveData<String>()


    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>>
        get() = _userList

    private val _messagesList = MutableLiveData<List<Message>>()
    val messagesList: LiveData<List<Message>>
        get() = _messagesList

    private val _companionUser = MutableLiveData<User?>()
    val companionUser: LiveData<User?>
        get() = _companionUser

    private val _isMessageSent = MutableLiveData<Boolean>()
    val isMessageSent: LiveData<Boolean>
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


    // TODO new data

    var newUserID = ""
    var newCompUserID = ""
    var newCompUserName= ""

    fun setAuthStateListener(listener: AuthStateListener) {
        authStateListener = listener
        auth.addAuthStateListener {
            val user = it.currentUser
            val userID = user?.uid
            if (userID != null) {
                newUserID = userID
            }
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
                    if (auth.currentUser?.uid == user.key){
                        val userFromDB = user.getValue(User::class.java)
                        if (userFromDB != null) {
                            _userName.value = userFromDB.name
                            _isUserOnline.value = userFromDB.online
                        }
                    }

                }
                _userList.value = listOfUsers
                Log.d("MainViewModel", "USERS: $listOfUsers" )
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MainViewModel", error.message)
            }

        })

        // TODO
        users.child(newCompUserID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                _companionUser.value = user
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatViewModel", "Error: " + error.message)
            }

        })



        messages.child(newUserID).child(newCompUserID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfMessages = arrayListOf<Message>()
                for (message in snapshot.children){
                    val messageFromDB = message.getValue(Message::class.java)
                    if (messageFromDB != null) {
                        listOfMessages.add(messageFromDB)
                    }
                }
                _messagesList.value = listOfMessages
                Log.d("ChatViewModel", "ListOfMessages: $listOfMessages")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatViewModel", "Error: " + error.message)
            }
        })

    }

    fun setUserOnline(isOnline: Boolean){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            users.child(userId).child("online").setValue(isOnline)
        }
    }


    // TODO
    fun sendMessage(message: Message){
        messages
            .child(message.senderID)
            .child(message.companionID)
            .push()
            .setValue(message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Message: $it")
                messages
                    .child(message.companionID)
                    .child(message.senderID)
                    .push()
                    .setValue(message)
                    .addOnSuccessListener {
                        _isMessageSent.value = true

                    }
                    .addOnFailureListener {
                        _error.value = it.message
                        Log.d("ChatViewModel", it.message.toString())
                    }
            }
            .addOnFailureListener {
                _error.value = it.message
                Log.d("ChatViewModel", it.message.toString())
            }

    }


    fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _isExistedUser.value = it.user
                val userID = it.user?.uid
                if (userID != null) {
                    newUserID = userID
                }
                Log.d("LoginViewModel", "Success: $it")
                Log.d("LoginViewModel", "User: $userID")
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
                newUserID = it.user?.uid.toString()

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