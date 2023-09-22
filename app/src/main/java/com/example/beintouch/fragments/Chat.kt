package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.beintouch.R
import com.example.beintouch.adapters.ChatAdapter
import com.example.beintouch.adapters.MessagesAdapter
import com.example.beintouch.databinding.FragmentChatBinding
import com.example.beintouch.databinding.FragmentChatsBinding
import com.example.beintouch.databinding.FragmentLoginBinding
import com.example.beintouch.presentation.ChatViewModel
import com.example.beintouch.presentation.ChatViewModelFactory
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.Message


class Chat : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var messagesAdapter: MessagesAdapter

    private lateinit var currentUserID: String
    private lateinit var companionUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        messagesAdapter = MessagesAdapter("1")
//        binding.recyclerViewChat.adapter = messagesAdapter
//        val chatViewModelFactory = ChatViewModelFactory(currentUserID, companionUserID)
//        chatViewModel = ViewModelProvider(this, chatViewModelFactory)[ChatViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        observeViewModel()
//        binding.buttonToSendMessage.setOnClickListener {
//            val textMessage = binding.inputMessageFromUser.text.toString().trim()
//            val message = Message(textMessage,currentUserID, companionUserID )
//            chatViewModel.sendMessage(message)
//        }

    }

    private fun observeViewModel(){
        chatViewModel.messagesList.observe(viewLifecycleOwner){
            messagesAdapter.submitList(it)
        }
        chatViewModel.companionUser.observe(viewLifecycleOwner){
            if (it != null) {
                binding.companionUserName.text = it.name
            }
        }
    }


    companion object {
        lateinit var userID: String
        lateinit var compID: String

        @JvmStatic
        fun newInstance() = Chat()
    }
}