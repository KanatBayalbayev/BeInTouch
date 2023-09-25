package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.beintouch.databinding.TestBinding
import com.example.beintouch.presentation.ChatViewModel
import com.example.beintouch.presentation.ChatViewModelFactory
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.Message


class Chat : Fragment() {
    private lateinit var binding: TestBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var messagesAdapter: MessagesAdapter


    private lateinit var currentUserID: String
    private lateinit var companionUserID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.userID.observe(viewLifecycleOwner){
            if (it != null) {
                currentUserID = it
            }
        }
        mainViewModel.companionID.observe(viewLifecycleOwner){
            if (it != null) {
                companionUserID = it
            }
        }
        val factory = ChatViewModelFactory(currentUserID,companionUserID)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToChatsFromChat()
        messagesAdapter = MessagesAdapter(currentUserID)
        binding.testRv.adapter = messagesAdapter




        observeViewModel()
        binding.buttonToSendMessage.setOnClickListener {
            val textMessage = binding.inputMessageFromUser.text.toString().trim()
            val message = Message(textMessage,currentUserID, companionUserID )
            mainViewModel.sendMessage(message)
        }

    }

    private fun backToChatsFromChat() {
        binding.buttonToBackToChatsFromChat.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Account.newInstance())
                ?.commit()
        }
    }

    private fun observeViewModel() {
        mainViewModel.messagesList.observe(viewLifecycleOwner) {
            messagesAdapter.submitList(it)
        }
        mainViewModel.companionUser.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.companionUserName.text = it.name
            }
        }
        mainViewModel.companionUser.observe(viewLifecycleOwner){
            if (it != null) {
                companionUserID = it.id
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = Chat()
    }
}