package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.beintouch.R
import com.example.beintouch.adapters.MessagesAdapter
import com.example.beintouch.databinding.TestBinding
import com.example.beintouch.presentation.ChatViewModel
import com.example.beintouch.presentation.ChatViewModelFactory
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.Message


class Chat : Fragment() {
    private lateinit var binding: TestBinding
//    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var messagesAdapter: MessagesAdapter


    private lateinit var currentUserID: String
    private lateinit var companionUserID: String
    private lateinit var viewModel: ChatViewModel


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        currentUserID = arguments?.getString(KEY_CURRENT_USER_ID).toString()
//        companionUserID = arguments?.getString(KEY_COMP_USER_ID).toString()
//
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserID = arguments?.getString(KEY_CURRENT_USER_ID).toString()
        companionUserID = arguments?.getString(KEY_COMP_USER_ID).toString()
        val viewModelFactory = ChatViewModelFactory(currentUserID, companionUserID)
        viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
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
            val message = Message(textMessage, currentUserID, companionUserID)
            viewModel.sendMessage(message)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserOnline(true)

    }

    override fun onPause() {
        super.onPause()
        viewModel.setUserOnline(false)
    }

    private fun backToChatsFromChat() {
        binding.buttonToBackToChatsFromChat.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Chats.newInstance(currentUserID))
                ?.commit()
        }
    }

    private fun observeViewModel() {
        viewModel.messagesList.observe(viewLifecycleOwner){
            messagesAdapter.submitList(it)
        }
        viewModel.companionUser.observe(viewLifecycleOwner){
            if (it != null) {
                binding.companionUserName.text = it.name
                if (it.online){
                    binding.statusCompUserChat.text = "online"
                } else {
                    binding.statusCompUserChat.text = "offline"
                }

            }
        }


    }


    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_COMP_USER_ID = "comp_user_id"

        @JvmStatic
        fun newInstance(parameterUserID: String,parameterCompUserID: String ): Chat {
            val fragment = Chat()
            val args = Bundle()
            args.putString(KEY_CURRENT_USER_ID, parameterUserID)
            args.putString(KEY_COMP_USER_ID, parameterCompUserID)
            fragment.arguments = args
            return fragment
        }
    }
}