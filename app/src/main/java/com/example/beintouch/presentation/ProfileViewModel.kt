package com.example.beintouch.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class ProfileViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: AuthStateListener? = null
    private val database = Firebase.database
    private val storage = Firebase.storage
    private val users = database.getReference("Users")
    private val friends = database.getReference("Friends")
    private val messages = database.getReference("Messages")


    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user


    fun getInfoAboutUser(currentUserID: String){
        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userDb in snapshot.children) {
                    val userID= userDb.key
                    if (userID != null) {
                        if (userID == currentUserID) {
                            _user.value = userDb.getValue(User::class.java)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ProfileTest", error.toString())
            }

        })
    }

    fun changeUserData(currentUserID: String,userName: String, userProfileImage: Uri? = null){
        if (userProfileImage != null) {
            uploadNewImageToFirebaseStorage(userProfileImage, currentUserId = currentUserID, username = userName)
        } else {
            uploadNewImageToFirebaseStorage(currentUserId = currentUserID, username = userName)

        }
    }
    private fun uploadNewImageToFirebaseStorage(
        userProfileImage: Uri? = null,
        currentUserId: String,
        username: String
    ) {
        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$filename")

        if (userProfileImage != null) {
            ref.putFile(userProfileImage)
                .addOnSuccessListener {
                    Log.d("ProfileViewModel", "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener{
                        val imageUser = it.toString()
                        Log.d("ProfileViewModel", "ReferenceImage: $it")
                        users.child(currentUserId).child("name").setValue(username)
                        users.child(currentUserId).child("userProfileImage").setValue(imageUser)
//                        users.addValueEventListener(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                for (userDb in snapshot.children) {
//                                    val userID= userDb.key
//                                    if (userID != null) {
//                                        if (userID == currentUserId) {
//                                            val userData = userDb.getValue(User::class.java)
//                                            Log.d("ProfileViewModel", "CurrentUser: $userData")
//                                            if (userData != null) {
//                                                userData.name = username
//                                                userData.userProfileImage = imageUser
//                                                users.child(currentUserId).setValue(userData)
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                Log.d("ProfileTest", error.toString())
//                            }
//
//                        })
                    }
                }
        } else {
            users.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    users.child(currentUserId).child("name").setValue(username)
//                    for (userDb in snapshot.children) {
//                        val userID= userDb.key
//                        if (userID != null) {
//                            if (userID == currentUserId) {
//                                val userData = userDb.getValue(User::class.java)
//                                Log.d("ProfileViewModel", "CurrentUser: $userData")
//                                if (userData != null) {
//                                    userData.name = username
//                                    users.child(currentUserId).setValue(userData)
//                                }
//                            }
//                        }
//                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ProfileTest", error.toString())
                }

            })

        }
    }






    fun logout() {
        setUserOnline(false)
        auth.signOut()

    }

    private fun setUserOnline(isOnline: Boolean) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            users.child(userId).child("online").setValue(isOnline)
        }
    }





}

