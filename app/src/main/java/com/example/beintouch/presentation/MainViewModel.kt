package com.example.beintouch.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class MainViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: AuthStateListener? = null
    private val database = Firebase.database
    private val storage = Firebase.storage
    private val users = database.getReference("Users")
    private val friends = database.getReference("Friends")
    private val messages = database.getReference("Messages")
    private var imageUser = ""

    private var previousMessage = ""
    private var currentUserID = ""

    private val _lastMessage = MutableLiveData<String>()
    val lastMessage: LiveData<String>
        get() = _lastMessage

    private val _foundUser = MutableLiveData<User?>()
    val foundUser: LiveData<User?>
        get() = _foundUser

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>>
        get() = _userList

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _isError= MutableLiveData<Boolean>()
    val isError: LiveData<Boolean>
        get() = _isError

    private val _isErrorReg= MutableLiveData<Boolean>()
    val isErrorReg: LiveData<Boolean>
        get() = _isErrorReg

    private val _isErrorFound= MutableLiveData<Boolean>()
    val isErrorFound: LiveData<Boolean>
        get() = _isErrorFound

    private val _noConnection= MutableLiveData<Boolean>()
    val noConnection: LiveData<Boolean>
        get() = _noConnection


    init {
        getLastMessage("3QMKol7gxFUML6PCJMh7SGnbV1h2", "rjpgDn1FMlWeVGQerVeswU4sL6P2")
        getTokenFCM()
    }

    fun getTokenFCM(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainView", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("TokenFCM", "Token: $token")
        })
    }

    fun readMessage(senderId: String, currentUserID: String, isMessageRead: Boolean) {
        messages
            .child(senderId)
            .child(currentUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (messageDB in snapshot.children) {
                        val sendID = messageDB.child("senderID").getValue(String::class.java)
                        if (sendID == senderId) {
                            val messageKey = messageDB.key
                            if (messageKey != null) {
                                messages
                                    .child(senderId)
                                    .child(currentUserID)
                                    .child(messageKey)
                                    .child("readByRecipient")
                                    .setValue(isMessageRead)

                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Tester", error.message)
                }

            })

    }


    fun findUser(email: String) {
        val emailUser = email.replaceFirst(email[0], email[0].lowercaseChar())
        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userDb in snapshot.children) {
                    val userEmail = userDb.child("email").getValue(String::class.java)
                    if (emailUser == userEmail) {
                        val userFromDB = userDb.getValue(User::class.java)
                        if (userFromDB != null) {
                            _foundUser.value = userFromDB
                            _isErrorFound.value = false
                            Log.d("FoundUser", userFromDB.name)
                        }
                    } else {
                        _isErrorFound.value = true
                        Log.d("FoundUser", "No such a user")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error", error.toString())
            }
        })
    }

    fun deleteChat(currentUserId: String, userIdForRemove: String) {
        val recordPath = "$currentUserId/$userIdForRemove"
        friends.child(recordPath).removeValue()
        messages.child(recordPath).removeValue()
    }

    private fun getLastMessage(currentUserId: String, companionUserId: String) {
        val query = messages.child(currentUserId).child(companionUserId).orderByKey().limitToLast(1)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val lastMessageData: Message? = childSnapshot.getValue(Message::class.java)
                        if (lastMessageData != null) {
                            previousMessage = lastMessageData.textMessage
                            _lastMessage.value = lastMessageData.textMessage
                            Log.d("LastMessage", "Last message: ${lastMessageData.textMessage}")
                        }
                    }
                } else {
                    Log.d("LastMessage", "No messages found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LastMessage", "Failed to retrieve last message: ${error.message}")
            }

        })

    }

    fun addFoundUserToChats(foundUser: User) {
        friends.child(auth.currentUser?.uid ?: "").child(foundUser.id).setValue(foundUser)
        friends.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfFriends = arrayListOf<User>()

                for (user in snapshot.children) {
                    if (user.key == auth.currentUser?.uid) {
                        for (friend in user.children) {
                            val friendFromDB = friend.getValue(User::class.java)

                            if (friendFromDB != null) {
                                listOfFriends.add(friendFromDB)
                            }
                        }
                    }
                }
                _userList.value = listOfFriends
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FoundUsers", error.toString())
            }
        })
    }

    fun setAuthStateListener(listener: AuthStateListener, currentUser: String = "") {
        authStateListener = listener
        auth.addAuthStateListener {
            val user = it.currentUser
            if (user != null) {
                authStateListener?.onUserAuthenticated(user)
            } else {
                authStateListener?.onUserUnauthenticated()
            }
        }
        friends.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfFriends = arrayListOf<User>()
                for (users in snapshot.children) {
                    val userKey = users.key
                    if (userKey == currentUser) {
                        for (friend in users.children) {

                            val friendFromDB = friend.getValue(User::class.java)
                            if (friendFromDB != null) {
                                listOfFriends.add(friendFromDB)
                            }
                        }
                    }
                }
                _userList.value = listOfFriends
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Friends", error.message)
            }

        })






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
                        val userId = auth.currentUser!!.uid
                        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userId")
                    }
                }
//                _userList.value = listOfUsers
                Log.d("MainViewModel", "USERS: $listOfUsers")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MainViewModel", error.message)
            }
        })
//        friends.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val listOfFriends = arrayListOf<User>()
//                for (friend in snapshot.children){
//                    if (auth.currentUser?.uid != friend.key) {
//                        val friendFromDB = friend.getValue(User::class.java)
//                        if (friendFromDB != null) {
//                            listOfFriends.add(friendFromDB)
//                        }
//                    }
//                }
//                _userList.value = listOfFriends
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("Friends", error.message)
//            }
//
//        })
    }

    fun setUserOnline(isOnline: Boolean) {
        val userId = auth.currentUser?.uid
        val timestamp = getCurrentTime()

        if (userId != null) {
            users.child(userId).child("online").setValue(isOnline)
            users.child(userId).child("lastTimeVisit").setValue(timestamp)
        }
    }

    private fun getCurrentTime(): String{
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return currentTime.format(formatter)
    }




    fun signInWithEmailAndPassword(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _isError.value = false
                    _noConnection.value = false
                } else {
                    val exception = it.exception
                    if (exception is FirebaseNetworkException) {
                        _noConnection.value = true
                        Log.d("NoConnection", "No Connection!!!")
                    }
                }
            }
//            .addOnSuccessListener {
//                _isError.value = false
//            }
//            .addOnFailureListener {
//                _errorMessage.value = it.message
//                _isError.value = true
//            }
    }

    fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        full_name: String
    ) {
        val emailUser = email.replaceFirst(email[0], email[0].lowercaseChar())
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _isErrorReg.value = false
                val userInfo = it.user

                val newUser = userInfo?.let { user ->
                    User(
                        user.uid,
                        emailUser,
                        password,
                        full_name,
                        true,
                    )
                }
                if (newUser != null) {
                    users.child(userInfo.uid).setValue(newUser)

                }



            }
            .addOnFailureListener {
                _isErrorReg.value = true
                _errorMessage.value = it.message
            }
//        uploadImageToFirebaseStorage(email, password, full_name, isOnline, userProfileImage)
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//
//                val userInfo = it.user
//
//                val newUser = userInfo?.let { user ->
//                    User(
//                        user.uid,
//                        email,
//                        password,
//                        full_name,
//                        userProfileImage = imageUser
//                        )
//                }
//                if (newUser != null) {
//                    users.child(userInfo.uid).setValue(newUser)
//                }
//
//
//
//
//            }
//            .addOnFailureListener {
//                _error.value = it.message
//            }
    }


    private fun uploadImageToFirebaseStorage(
        email: String,
        password: String,
        full_name: String,
        isOnline: Boolean,
        userProfileImage: Uri
    ) {
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$filename")
        ref.putFile(userProfileImage)
            .addOnSuccessListener { it ->
                Log.d("Registration", "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    val imageUser = it.toString()
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val userInfo = it.user

                            val newUser = userInfo?.let { user ->
                                User(
                                    user.uid,
                                    email,
                                    password,
                                    full_name,
                                    false,
                                    imageUser
                                )
                            }
                            if (newUser != null) {
                                users.child(userInfo.uid).setValue(newUser)

                            }


                        }
                        .addOnFailureListener {
                            _errorMessage.value = it.message
                        }
                    Log.d("Registration", "File location: $it")
                }
            }

    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _success.value = true
            }
            .addOnFailureListener {
                _errorMessage.value = it.message
            }

    }

    fun logout() {
        setUserOnline(false)
        auth.signOut()
    }

}