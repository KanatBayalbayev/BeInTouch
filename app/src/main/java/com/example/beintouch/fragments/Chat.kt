package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beintouch.R
import com.example.beintouch.adapters.MessAdapter
import com.example.beintouch.adapters.MessagesAdapter
import com.example.beintouch.databinding.ChatBinding
import com.example.beintouch.presentation.ChatViewModel
import com.example.beintouch.presentation.ChatViewModelFactory
import com.example.beintouch.presentation.Message
import com.example.beintouch.presentation.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Chat : Fragment() {
    private lateinit var binding: ChatBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messAdapter: MessAdapter
    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var reference1: DatabaseReference
    private lateinit var reference2: DatabaseReference
    private val database = Firebase.database

    private lateinit var currentUserID: String
    private lateinit var companionUserID: String
    private var isReadMessage: Boolean = false
    private lateinit var currentUser: User
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var seenListener1: ValueEventListener
    private lateinit var seenListener2: ValueEventListener
    private var listOfMess = listOf<Message>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserID = arguments?.getString(KEY_CURRENT_USER_ID).toString()
        companionUserID = arguments?.getString(KEY_COMP_USER_ID).toString()
        isReadMessage = arguments?.getBoolean(KEY_IS_READ_MESSAGE) == true
        val viewModelFactory = ChatViewModelFactory(currentUserID, companionUserID)
        chatViewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
        binding = ChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToChatsFromChat()
        observeViewModel()
        val data = listOf(
            Message("hello", currentUserID, companionUserID, "12:05", false),
            Message("hello2", currentUserID, companionUserID, "12:05", false),
            Message("hello3", currentUserID, companionUserID, "12:05", false),
            Message("hello4", companionUserID, currentUserID, "12:05", false),
            Message("hello5", currentUserID, companionUserID, "12:05", false),
        )
        Log.d("Checker", listOfMess.toString())
//        messagesAdapter = MessagesAdapter(currentUserID, companionUserID, isReadMessage)
//        messAdapter = MessAdapter(listOfMess, currentUserID, companionUserID)
//
//        binding.testRv.layoutManager = LinearLayoutManager(requireContext())
//        binding.testRv.adapter = messAdapter
//        chatViewModel.readMessage(companionUserID, currentUserID, true)


        seenMessage(companionUserID)
        binding.buttonToSendMessage.setOnClickListener {
            val textMessage = binding.inputMessageFromUser.text.toString().trim()
            val message =
                Message(textMessage, currentUserID, companionUserID, getCurrentTime(), false)
            chatViewModel.sendMessage(message, currentUser)
        }

    }


    private fun seenMessage(userId: String) {
        reference1 = database.getReference("Messages").child(companionUserID )
            .child(auth.currentUser?.uid ?: "")
        seenListener1 = reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val chat = snap.getValue(Message::class.java)
                    Log.d("CheckTest", snap.toString())
                    if (chat?.companionID == auth.currentUser?.uid && chat?.senderID == companionUserID) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snap.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Tester", "it is not working")
            }

        })
//        reference2 = database.getReference("Messages").child(companionUserID).child(currentUserID)
//        seenListener2 = reference2.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (snap in snapshot.children) {
//
//                    val chat = snap.getValue(Message::class.java)
//                    Log.d("CheckTest", snap.toString())
//                    if (chat?.companionID == userId && chat.senderID == currentUserID) {
//                        val hashMap = HashMap<String, Any>()
//                        hashMap["isseen"] = true
//                        snap.ref.updateChildren(hashMap)
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("Tester", "it is not working")
//            }
//
//        })

    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return dateFormat.format(currentTime)
    }

    override fun onResume() {
        super.onResume()
        chatViewModel.setUserOnline(true)
    }

    override fun onPause() {
        super.onPause()
        reference1.removeEventListener(seenListener1)
//        reference2.removeEventListener(seenListener2)
        chatViewModel.setUserOnline(false)
    }

    private fun backToChatsFromChat() {
        binding.buttonToBackToChatsFromChat.setOnClickListener {
            requireFragmentManager().beginTransaction()
                .replace(R.id.container, Chats.newInstance(currentUserID))
                .commit()
        }
    }

    private fun observeViewModel() {
        chatViewModel.messagesList.observe(viewLifecycleOwner) {
            Log.d("Checker", "InsideOB: $it")
            messAdapter = MessAdapter(it, currentUserID, companionUserID)

            binding.testRv.layoutManager = LinearLayoutManager(requireContext())
            binding.testRv.adapter = messAdapter
            binding.testRv.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    if (it.isEmpty()) {
                        return
                    }
                    binding.testRv.scrollToPosition(it.size - 1)
                    binding.testRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            })
//            messagesAdapter.submitList(it){
//                if (it.isEmpty()){
//                    return@submitList
//                }
////                binding.testRv.smoothScrollToPosition(it.size - 1)
//            }
        }
        chatViewModel.companionUser.observe(viewLifecycleOwner) {
            if (it != null) {
                Picasso.get().load(it.userProfileImage).into(binding.userIcon)
                binding.companionUserName.text = it.name
                if (it.online) {
                    binding.statusCompUserChat.setText(R.string.statusOnline)
                    binding.statusCompUserChat.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.greenOnline
                        )
                    )
                } else {
                    binding.statusCompUserChat.setText(R.string.statusOffline)
                    binding.statusCompUserChat.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.redOffline
                        )
                    )
                }

            }
        }
        chatViewModel.currUser.observe(viewLifecycleOwner) {
            if (it != null) {
                currentUser = it
            }
        }


    }


    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_COMP_USER_ID = "comp_user_id"
        private const val KEY_IS_READ_MESSAGE = "is_read_message"

        @JvmStatic
        fun newInstance(
            parameterUserID: String,
            parameterCompUserID: String,
            parameterIsRead: Boolean
        ): Chat {
            val fragment = Chat()
            val args = Bundle()
            args.putString(KEY_CURRENT_USER_ID, parameterUserID)
            args.putString(KEY_COMP_USER_ID, parameterCompUserID)
            args.putBoolean(KEY_IS_READ_MESSAGE, parameterIsRead)
            fragment.arguments = args
            return fragment
        }
    }
}