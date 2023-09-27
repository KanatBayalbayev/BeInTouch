package com.example.beintouch.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatViewModel(
    private val currentUserID: String,
    private val companionID: String,
): ViewModel() {
    private val database = Firebase.database
    private val users = database.getReference("Users")
    private val userIdToSearch = "Kanatkz07@mail.ru"
    private val query = users.orderByChild("email").equalTo(userIdToSearch)

    private val messages = database.getReference("Messages")

    private val _messagesList = MutableLiveData<List<Message>>()
    val messagesList: LiveData<List<Message>>
        get() = _messagesList

    private val _companionUser = MutableLiveData<User?>()
    val companionUser: LiveData<User?>
        get() = _companionUser

    private val _isMessageSent = MutableLiveData<Boolean>()
    val isMessageSend: LiveData<Boolean>
        get() = _isMessageSent

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    init {
        med()
        findUser("Kanat@gmail.com")
    }

    private fun findUser(email: String){
        users.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userDb in snapshot.children){
                    val userEmail = userDb.child("email").getValue(String::class.java)
                    if (email == userEmail) {
                        Log.d("FindUserModel", "Found UserEmail: $email")
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun med(){
        users.child(companionID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                _companionUser.value = user
                Log.d("ChatViewModel", "CompUser: $user")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatViewModel", "ErrorOFcompanionUser: " + error.message)
            }

        })
        messages.child(currentUserID).child(companionID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfMessages = arrayListOf<Message>()
                for (message in snapshot.children){
                    val messageFromDB = message.getValue(Message::class.java)
                    if (messageFromDB != null) {
                        listOfMessages.add(messageFromDB)
                    }
                }
                _messagesList.value = listOfMessages
                Log.d("ChatViewModel", "Messages: $listOfMessages")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatViewModel", "ErrorOFmessages: " + error.message)
            }
        })

    }
    fun setUserOnline(isOnline: Boolean){
        users.child(currentUserID).child("online").setValue(isOnline)
    }

    fun sendMessage(message: Message){
        messages
            .child(message.senderID)
            .child(message.companionID)
            .push()
            .setValue(message)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "MessageOfUser: $it")
                messages
                    .child(message.companionID)
                    .child(message.senderID)
                    .push()
                    .setValue(message)
                    .addOnSuccessListener {
                        _isMessageSent.value = true
                        Log.d("ChatViewModel", "MessageOfCompanion: $it")

                    }
                    .addOnFailureListener {
                        _error.value = it.message
                        Log.d("ChatViewModel", "MessageErrorOfCompanion: " + it.message.toString())
                    }
            }
            .addOnFailureListener {
                _error.value = it.message
                Log.d("ChatViewModel", it.message.toString())
                Log.d("ChatViewModel", "MessageErrorOfCurrentUser: " + it.message.toString())
            }

    }
}