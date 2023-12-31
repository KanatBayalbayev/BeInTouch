package com.example.beintouch.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beintouch.R
import com.example.beintouch.fragments.Data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class ChatViewModel(
    private val currentUserID: String,
    private val companionID: String,
) : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val database = Firebase.database
    private val storage = Firebase.storage
    private val users = database.getReference("Users")
    private val userIdToSearch = "Kanatkz07@mail.ru"
    private val query = users.orderByChild("email").equalTo(userIdToSearch)
    var userIsTyping = false
    private val messages = database.getReference("Messages")
    private val friends = database.getReference("Friends")
    private val tokens = database.getReference("Tokens")
    private lateinit var reference: DatabaseReference
    private lateinit var seenListener: ValueEventListener

    var notify = false


    private val _messagesList = MutableLiveData<List<Message>>()
    val messagesList: LiveData<List<Message>>
        get() = _messagesList

    private val _currUser = MutableLiveData<User?>()
    val currUser: LiveData<User?>
        get() = _currUser

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
        seenMessage()
    }


    private fun med() {
        users.child(companionID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                _companionUser.value = user
                Log.d("ChatViewModel", "CompUser: $user")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatViewModel", "ErrorOFcompanionUser: " + error.message)
            }

        })
        users.child(currentUserID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

//                if (user != null) {
//                    user.isTyping = userIsTyping
//
//                }
                _currUser.value = user
//                users.child(currentUserID).setValue(user)
                Log.d("ChatViewModel", "CompUser: $user")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatViewModel", "ErrorOFcompanionUser: " + error.message)
            }

        })
        messages.child(currentUserID).child(companionID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listOfMessages = arrayListOf<Message>()
                    for (message in snapshot.children) {
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
//        textWatch()

    }

    fun textWatch(isTyping: Boolean) {
        users.child(currentUserID).child("typing").setValue(isTyping)
//        users.child(currentUserID).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val user = snapshot.getValue(User::class.java)
//                if (user != null) {
//                    user.isTyping = isTyping
//                    users.child(currentUserID).setValue(user)
//                }
//                Log.d("ChatIsTyping", "User: $user")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("ChatIsTyping", "Error: $error")
//            }
//
//        })
    }

    fun changeUserData(currentUserId: String, username: String, userProfileImage: Uri) {
        uploadNewImageToFirebaseStorage(userProfileImage, currentUserId, username)
    }

    private fun uploadNewImageToFirebaseStorage(
        userProfileImage: Uri,
        currentUserID: String,
        username: String
    ) {
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$filename")
        ref.putFile(userProfileImage)
            .addOnSuccessListener {
                Log.d("ProfileViewModel", "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    val imageUser = it.toString()
                    users.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userDb in snapshot.children) {
                                val userID = userDb.key
                                if (userID != null) {
                                    if (userID == currentUserID) {
                                        val userData = userDb.getValue(User::class.java)
                                        if (userData != null) {
                                            userData.name = username
                                            userData.userProfileImage = imageUser
                                            users.child(currentUserID).setValue(userData)
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("ProfileTest", error.toString())
                        }

                    })
                }
            }
    }


    fun logout() {
        setUserOnline(false)
        auth.signOut()

    }

    fun setUserOnline(isOnline: Boolean) {
        val timestamp = getCurrentTime()
        users.child(currentUserID).child("online").setValue(isOnline)
        users.child(currentUserID).child("lastTimeVisit").setValue(timestamp)
    }

    private fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return currentTime.format(formatter)
    }

    fun readMessage(currentUserID: String, userId: String) {
        messages
            .child(currentUserID)
            .child(currentUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
                        val chat = snap.getValue(Message::class.java)
                        if (chat?.companionID == auth.currentUser?.uid && chat?.senderID == userId) {
                            val hashMap = HashMap<String, Any>()
                            hashMap["isseen"] = true
                            snap.ref.updateChildren(hashMap)

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Tester", error.message)
                }

            })

    }

    fun sendMessage(message: Message, foundUser: User) {
        friends.child(message.companionID).child(message.senderID).setValue(foundUser)
        friends
            .child(message.companionID)
            .child(message.senderID)
            .child("lastMessage")
            .setValue(message.textMessage)
        friends
            .child(message.companionID)
            .child(message.senderID)
            .child("lastTimeMessageSent")
            .setValue(message.timestamp)
        friends
            .child(message.senderID)
            .child(message.companionID)
            .child("lastMessage")
            .setValue(message.textMessage)
        friends
            .child(message.senderID)
            .child(message.companionID)
            .child("lastTimeMessageSent")
            .setValue(message.timestamp)

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

    private fun seenMessage(){
        reference = database.getReference("Messages").child(companionID)
            .child(auth.currentUser?.uid ?: "")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val chat = snap.getValue(Message::class.java)
                    Log.d("CheckTest", snap.toString())
                    if (chat?.companionID == auth.currentUser?.uid && chat?.senderID == companionID) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snap.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun removeEventListener(){
        reference.removeEventListener(seenListener)
    }


}